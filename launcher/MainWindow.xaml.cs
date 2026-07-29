using System;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Media.Effects;

namespace LovanLauncher
{
    public partial class MainWindow : Window
    {
        private ShakeAnimation _shakeAnimation;

        public MainWindow()
        {
            InitializeComponent();
            _shakeAnimation = new ShakeAnimation(this);
        }

        private void Window_DragMove(object sender, MouseButtonEventArgs e)
        {
            DragMove();
        }

        private async void PlayButton_Click(object sender, RoutedEventArgs e)
        {
            string username = UsernameBox?.Text?.Trim() ?? "";
            if (string.IsNullOrEmpty(username))
            {
                MessageBox.Show("Введите никнейм", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Warning);
                return;
            }
            
            await LaunchGame(username);
        }

        private async Task LaunchGame(string username)
        {
            try
            {
                string appData = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
                string gameDir = Path.Combine(appData, ".minecraft");
                
                if (!Directory.Exists(gameDir))
                {
                    MessageBox.Show($"Папка игры не найдена: {gameDir}", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Error);
                    return;
                }

                // Find Java 21 with aggressive detection
                string javaExe = await FindJavaPathAsync();
                if (string.IsNullOrEmpty(javaExe))
                {
                    javaExe = "javaw.exe"; // Fallback
                }

                // Build classpath with background scanning
                string classpath = await BuildClasspathAsync(gameDir);
                if (string.IsNullOrEmpty(classpath))
                {
                    MessageBox.Show("Не удалось собрать classpath", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Error);
                    return;
                }

                // Fabric 1.21.4 main class
                string mainClass = "net.fabricmc.loader.impl.launch.knot.KnotClient";

                // Build arguments with max performance settings
                string args = $"-Xmx6G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=50 " +
                            $"-XX:+UnlockExperimentalVMOptions -Dlog4j2.formatMsgNoLookups=true " +
                            $"-Djava.library.path=\"{Path.Combine(gameDir, "natives")}\" " +
                            $"-cp \"{classpath}\" {mainClass} " +
                            $"--username {username} --version 1.21.4 --gameDir \"{gameDir}\" " +
                            $"--assetsDir \"{Path.Combine(gameDir, "assets")}\" --assetIndex 17 " +
                            $"--uuid {Guid.NewGuid()} --accessToken 0 --userType legacy " +
                            $"--versionType Fabric --assetsIndexName 1.21.4";

                var psi = new ProcessStartInfo
                {
                    FileName = javaExe,
                    Arguments = args,
                    WorkingDirectory = gameDir,
                    UseShellExecute = false,
                    CreateNoWindow = true,
                    RedirectStandardError = true,
                    RedirectStandardOutput = true
                };

                using Process proc = Process.Start(psi);
                if (proc == null)
                {
                    throw new Exception("Не удалось запустить процесс Java");
                }

                // Start async read of output to prevent buffer deadlock
                _ = proc.BeginOutputReadLine();
                _ = proc.BeginErrorReadLine();

                // Wait for process to exit
                await proc.WaitForExitAsync();

                // Optional: check exit code
                if (proc.ExitCode != 0)
                {
                    throw new Exception(f"Java process exited with code {proc.ExitCode}");
                }

                // Success - fade out and close
                await FadeOutAndClose();
            }
            catch (Exception ex)
            {
                // On error: shake window and show detailed error
                await _shakeAnimation.Shake();
                MessageBox.Show($"Ошибка запуска:\n{ex.Message}\n\n{ex.StackTrace}", 
                              "Критическая ошибка", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        private Task<string> FindJavaPathAsync()
        {
            return Task.Run(() =>
            {
                string javaHome = Environment.GetEnvironmentVariable("JAVA_HOME");
                if (!string.IsNullOrEmpty(javaHome))
                {
                    string javaExe = Path.Combine(javaHome, "bin", "javaw.exe");
                    if (File.Exists(javaExe))
                        return javaExe;
                }

                // Scan standard Java paths
                string programFiles = Environment.GetFolderPath(Environment.SpecialFolder.ProgramFiles);
                string[] paths = {
                    Path.Combine(programFiles, "Java", "jdk-21", "bin", "javaw.exe"),
                    Path.Combine(programFiles, "Java", "jre-21", "bin", "javaw.exe"),
                    Path.Combine(programFiles, "Java", "jdk-22", "bin", "javaw.exe"),
                    Path.Combine(programFiles, "Java", "jre-22", "bin", "javaw.exe")
                };

                return paths.FirstOrDefault(File.Exists);
            });
        }

        private Task<string> BuildClasspathAsync(string gameDir)
        {
            return Task.Run(() =>
            {
                try
                {
                    string librariesPath = Path.Combine(gameDir, "libraries");
                    string versionPath = Path.Combine(gameDir, "versions", "1.21.4-Fabric");
                    
                    if (!Directory.Exists(versionPath))
                    {
                        versionPath = Path.Combine(gameDir, "versions", "1.21.4-fabric");
                        if (!Directory.Exists(versionPath))
                        {
                            return null;
                        }
                    }

                    // Get all JAR files from libraries
                    var libraryJars = Directory.Exists(librariesPath)
                        ? Directory.GetFiles(librariesPath, "*.jar", SearchOption.AllDirectories)
                        : new string[0];

                    // Get version-specific JARs
                    var versionJars = Directory.GetFiles(versionPath, "*.jar", SearchOption.TopDirectoryOnly);

                    // Combine all paths
                    var allJars = libraryJars.Concat(versionJars).ToArray();
                    if (allJars.Length == 0)
                        return null;

                    return string.Join(";", allJars);
                }
                catch
                {
                    return null;
                }
            });
        }

        private async Task FadeOutAndClose()
        {
            var storyboard = new Storyboard();
            var animation = new DoubleAnimation
            {
                From = 1.0,
                To = 0.0,
                Duration = TimeSpan.FromSeconds(3)
            };
            
            Storyboard.SetTarget(animation, this);
            Storyboard.SetTargetProperty(animation, new PropertyPath("Opacity"));
            storyboard.Children.Add(animation);
            
            storyboard.Completed += (s, _) => Application.Current.Shutdown();
            storyboard.Begin();
            
            await Task.Delay(3000);
        }

        private void UsernameBox_GotFocus(object sender, RoutedEventArgs e)
        {
            UsernameBox.BorderBrush = new SolidColorBrush(Color.FromRgb(90, 42, 131)); // Purple
            UsernameBox.Effect = new DropShadowEffect 
            { 
                Color = Color.FromRgb(90, 42, 131), 
                BlurRadius = 15, 
                Opacity = 0.8 
            };
            
            var storyboard = (Storyboard)FindResource("UsernameFocusStoryboard");
            storyboard?.Begin();
        }

        private void UsernameBox_LostFocus(object sender, RoutedEventArgs e)
        {
            UsernameBox.BorderBrush = new SolidColorBrush(Color.FromRgb(90, 42, 131)); // Keep purple border
            UsernameBox.Effect = null;
        }
    }

    public class StringEmptyToVisibilityConverter : System.Windows.Data.IValueConverter
    {
        public object Convert(object value, System.Type targetType, object parameter, System.Globalization.CultureInfo culture)
            => string.IsNullOrEmpty(value as string) ? Visibility.Visible : Visibility.Collapsed;

        public object ConvertBack(object value, System.Type targetType, object parameter, System.Globalization.CultureInfo culture)
            => throw new NotImplementedException();
    }

    public class ShakeAnimation
    {
        private readonly Window _window;
        private readonly DoubleAnimation _animation;
        private readonly Storyboard _storyboard;

        public ShakeAnimation(Window window)
        {
            _window = window;
            _animation = new DoubleAnimation
            {
                From = 0,
                To = 10,
                Duration = TimeSpan.FromMilliseconds(50),
                AutoReverse = true,
                RepeatBehavior = new RepeatBehavior(3)
            };
            
            _storyboard = new Storyboard();
            Storyboard.SetTarget(_animation, window);
            Storyboard.SetTargetProperty(_animation, new PropertyPath("(RenderTransform).(TranslateTransform.X)"));
            _storyboard.Children.Add(_animation);
        }

        public Task Shake()
        {
            var tcs = new TaskCompletionSource<bool>();
            
            // Ensure window has a render transform
            if (_window.RenderTransform is not TranslateTransform transform)
            {
                _window.RenderTransform = new TranslateTransform();
                transform = (TranslateTransform)_window.RenderTransform;
            }
            
            _storyboard.Completed += (s, e) => tcs.TrySetResult(true);
            _storyboard.Begin();
            
            return tcs.Task;
        }
    }
}