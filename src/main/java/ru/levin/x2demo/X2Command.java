package ru.levin.x2demo;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionManager;
import ru.levin.manager.ClientManager;
import ru.levin.manager.commandManager.Command;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class X2Command extends Command {

    public X2Command() {
        super("x2");
    }

    @Override
    public void execute(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            Function module = FunctionManager.get("X2Duplicator");
            if (module == null) {
                ClientManager.message(Formatting.RED + "[x2] Module not found!");
                return SINGLE_SUCCESS;
            }
            module.toggle();
            ClientManager.message("[x2] " + module.name + ": " + (module.isState() ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled"));
            return SINGLE_SUCCESS;
        });
    }
}
