package setting;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import core.Localization;
import java.util.function.Supplier;

public abstract class Setting {
   @Expose
   protected String name;
   @Expose
   protected String description;
   @Expose
   protected boolean visible = true;
   protected transient Runnable onChange;
   protected transient Runnable onVisibilityChange;
   protected transient Supplier<Boolean> visibilitySupplier;

   public Setting(String s, String s1) {
      this.name = s;
      this.description = s1;
   }

   public String getDisplayName() {
      return Localization.a().c(this.name);
   }

   public String getDisplayDescription() {
      return Localization.a().c(this.description);
   }

   public void setVisibilitySupplier(Supplier<Boolean> supplier) {
      this.visibilitySupplier = supplier;
   }

   public boolean updateVisibility() {
      if (this.visibilitySupplier != null) {
         boolean flag = this.visibilitySupplier.get();
         if (flag != this.visible) {
            this.visible = flag;
            this.onVisibilityChanged();
            return true;
         }
      }

      return false;
   }

   public boolean isVisible() {
      return this.visibilitySupplier != null ? this.visibilitySupplier.get() : this.visible;
   }

   protected void onVisibilityChanged() {
      if (this.onVisibilityChange != null) {
         try {
            this.onVisibilityChange.run();
            return;
         } catch (Exception exception) {
            System.err.println(exception.getMessage());
         }
      }
   }

   public abstract JsonObject toJson();

   public abstract void fromJson(JsonObject jsonobject);

   public abstract String getType();

   public abstract void reset();

   public void notifyChange() {
      if (this.onChange != null) {
         try {
            this.onChange.run();
            return;
         } catch (Exception exception) {
            System.err.println(exception.getMessage());
         }
      }
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public Runnable getOnChange() {
      return this.onChange;
   }

   public Runnable getOnVisibilityChange() {
      return this.onVisibilityChange;
   }

   public Supplier<Boolean> getVisibilitySupplier() {
      return this.visibilitySupplier;
   }

   public void setVisible(boolean flag) {
      this.visible = flag;
   }

   public void setOnChange(Runnable runnable) {
      this.onChange = runnable;
   }

   public void setOnVisibilityChange(Runnable runnable) {
      this.onVisibilityChange = runnable;
   }
}
