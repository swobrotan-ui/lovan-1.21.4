package ru.levin.modules.player;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import ru.levin.events.Event;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;

@SuppressWarnings("All")
@FunctionAnnotation(name = "Dupe", desc = "Дупликация предметов при выбросе (Q). Только для операторов сервера.", type = Type.Player)
public class Dupe extends Function {

    @Override
    public void onEvent(final Event event) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}