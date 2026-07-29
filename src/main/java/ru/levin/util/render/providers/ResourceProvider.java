package ru.levin.util.render.providers;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public final class ResourceProvider {
	public static final ShaderProgramKey TEXTURE_SHADER_KEY = new ShaderProgramKey(getShaderIdentifier("texture"), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY);
	public static final ShaderProgramKey RECTANGLE_SHADER_KEY = new ShaderProgramKey(getShaderIdentifier("rectangle"), VertexFormats.POSITION_COLOR, Defines.EMPTY);
	public static final ShaderProgramKey BLUR_SHADER_KEY = new ShaderProgramKey(getShaderIdentifier("blur"), VertexFormats.POSITION_COLOR, Defines.EMPTY);
	public static final ShaderProgramKey MOTIONBLUR_SHADER_KEY = new ShaderProgramKey(getShaderIdentifier("motionblur"), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY);
	public static final ShaderProgramKey RECTANGLE_BORDER_SHADER_KEY = new ShaderProgramKey(getShaderIdentifier("border"), VertexFormats.POSITION_COLOR, Defines.EMPTY);
	public static final ShaderProgramKey GLASS_SHADER_KEY = new ShaderProgramKey(getGlass("data"), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY);

	public static final Identifier firefly = Identifier.of("sodiumextra", "images/particles/firefly.png");
	public static final Identifier bloom = Identifier.of("sodiumextra", "images/particles/bloom.png");
	public static final Identifier snowflake = Identifier.of("sodiumextra", "images/particles/snowflake.png");
	public static final Identifier dollar = Identifier.of("sodiumextra", "images/particles/dollar.png");
	public static final Identifier heart = Identifier.of("sodiumextra", "images/particles/heart.png");
	public static final Identifier star = Identifier.of("sodiumextra", "images/particles/star.png");
	public static final Identifier spark = Identifier.of("sodiumextra", "images/particles/spark.png");
	public static final Identifier crown = Identifier.of("sodiumextra", "images/particles/crown.png");
	public static final Identifier lightning = Identifier.of("sodiumextra", "images/particles/lightning.png");
	public static final Identifier line = Identifier.of("sodiumextra", "images/particles/line.png");
	public static final Identifier point = Identifier.of("sodiumextra", "images/particles/point.png");
	public static final Identifier rhombus = Identifier.of("sodiumextra", "images/particles/rhombus.png");


	public static final Identifier marker = Identifier.of("sodiumextra", "images/targetesp/target.png");
	public static final Identifier marker2 = Identifier.of("sodiumextra", "images/targetesp/target2.png");
	public static final Identifier marker3 = Identifier.of("sodiumextra", "images/targetesp/marker.png");


	public static final Identifier CUSTOM_CAPE = Identifier.of("sodiumextra", "cape/cape.png");
	public static final Identifier CUSTOM_ELYTRA = Identifier.of("sodiumextra", "cape/elytra.png");

	public static final Identifier container = Identifier.of("sodiumextra", "images/hud/container.png");

	public static final Identifier color_image = Identifier.of("sodiumextra", "images/gui/pick.png");


	private static Identifier getGlass(String name) {
		return Identifier.of("sodiumextra", "core/glass/" + name);
	}
	private static Identifier getShaderIdentifier(String name) {
		return Identifier.of("sodiumextra", "core/" + name);
	}
}