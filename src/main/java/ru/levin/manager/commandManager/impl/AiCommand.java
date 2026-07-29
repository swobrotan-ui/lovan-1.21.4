package ru.levin.manager.commandManager.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;
import ru.levin.manager.ClientManager;
import ru.levin.manager.Manager;
import ru.levin.manager.commandManager.Command;
import ru.levin.modules.combat.AttackAura;

import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class AiCommand extends Command {

    public AiCommand() {
        super("ai");
    }

    @Override
    public void execute(LiteralArgumentBuilder<CommandSource> root) {
        root.executes(ctx -> {
            AttackAura aura = Manager.FUNCTION_MANAGER.attackAura;
            aura.toggleAILearning();
            if (aura.isAiLearning()) {
                ClientManager.message(Formatting.GREEN + "AI: тренировка включена (бей кубики) " + aura.getAiTrainHits() + "/" + aura.getAiTrainMaxHits());
            } else {
                ClientManager.message(Formatting.YELLOW + "AI: тренировка выключена");
            }
            return SINGLE_SUCCESS;
        });

        root.then(literal("save")
                .then(arg("name", StringArgumentType.word())
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "name");
                            AttackAura aura = Manager.FUNCTION_MANAGER.attackAura;
                            boolean ok = aura.saveAiProfile(name);
                            if (ok) {
                                ClientManager.message(Formatting.GREEN + "AI: профиль сохранён как " + name);
                            } else {
                                ClientManager.message(Formatting.RED + "AI: мало данных. Запусти тренировку .ai и набей хотя бы немного ударов по кубикам");
                            }
                            return SINGLE_SUCCESS;
                        })));

        root.then(literal("load")
                .then(arg("name", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            AttackAura aura = Manager.FUNCTION_MANAGER.attackAura;
                            List<String> list = aura.listAiProfiles();
                            for (String s : list) builder.suggest(s);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "name");
                            AttackAura aura = Manager.FUNCTION_MANAGER.attackAura;
                            boolean ok = aura.loadAiProfile(name);
                            if (ok) {
                                ClientManager.message(Formatting.GREEN + "AI: профиль загружен: " + name);
                            } else {
                                ClientManager.message(Formatting.RED + "AI: профиль не найден: " + name);
                            }
                            return SINGLE_SUCCESS;
                        })));

        root.then(literal("list")
                .executes(ctx -> {
                    AttackAura aura = Manager.FUNCTION_MANAGER.attackAura;
                    List<String> list = aura.listAiProfiles();
                    if (list.isEmpty()) {
                        ClientManager.message(Formatting.GRAY + "AI: профилей нет");
                    } else {
                        ClientManager.message(Formatting.GRAY + "AI профили:");
                        for (String s : list) {
                            ClientManager.message(Formatting.GRAY + "- " + s);
                        }
                    }
                    return SINGLE_SUCCESS;
                }));
    }
}
