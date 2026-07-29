package shader;

import java.util.ArrayList;
import java.util.List;

public class EntityOutlineResources {
   private final List<ShaderResource> resources = new ArrayList<ShaderResource>();
   private static volatile boolean needsReload = false;

   public EntityOutlineResources() {
      this.a();
   }

   private void a() {
      this.resources.clear();
      this.resources.add(ShaderResource.b("post_effect/custom_entity_outline.json", c()));
      this.resources.add(ShaderResource.b("shaders/post/entity_outline_box_blur.json", d()));
      this.resources.add(ShaderResource.b("shaders/post/entity_outline_box_blur.fsh", e()));
      this.resources.add(ShaderResource.b("shaders/post/entity_outline_composite.json", g()));
      this.resources.add(ShaderResource.b("shaders/post/entity_outline_composite.fsh", h()));
      this.resources.add(ShaderResource.b("shaders/post/blur.vsh", f()));
   }

   public List<ShaderResource> getResources() {
      if (needsReload) {
         this.a();
         needsReload = false;
      }

      return this.resources;
   }

   private static byte[] c() {
      return "{\n    \"targets\": {\n        \"swap\": {},\n        \"original\": {}\n    },\n    \"passes\": [\n        {\n            \"program\": \"minecraft:post/entity_outline\",\n            \"inputs\": [\n                {\n                    \"sampler_name\": \"In\",\n                    \"target\": \"minecraft:entity_outline\"\n                }\n            ],\n            \"output\": \"swap\"\n        },\n        {\n            \"program\": \"minecraft:post/blit\",\n            \"inputs\": [\n                {\n                    \"sampler_name\": \"In\",\n                    \"target\": \"swap\"\n                }\n            ],\n            \"output\": \"original\"\n        },\n        {\n            \"program\": \"minecraft:post/entity_outline_box_blur\",\n            \"inputs\": [\n                {\n                    \"sampler_name\": \"In\",\n                    \"target\": \"swap\",\n                    \"bilinear\": true\n                }\n            ],\n            \"output\": \"minecraft:entity_outline\",\n            \"uniforms\": [\n                {\n                    \"name\": \"BlurDir\",\n                    \"values\": [ 1.0, 0.0 ]\n                }\n            ]\n        },\n        {\n            \"program\": \"minecraft:post/entity_outline_box_blur\",\n            \"inputs\": [\n                {\n                    \"sampler_name\": \"In\",\n                    \"target\": \"minecraft:entity_outline\",\n                    \"bilinear\": true\n                }\n            ],\n            \"output\": \"swap\",\n            \"uniforms\": [\n                {\n                    \"name\": \"BlurDir\",\n                    \"values\": [ 0.0, 1.0 ]\n                }\n            ]\n        },\n        {\n            \"program\": \"minecraft:post/entity_outline_box_blur\",\n            \"inputs\": [\n                {\n                    \"sampler_name\": \"In\",\n                    \"target\": \"swap\",\n                    \"bilinear\": true\n                }\n            ],\n            \"output\": \"minecraft:entity_outline\",\n            \"uniforms\": [\n                {\n                    \"name\": \"BlurDir\",\n                    \"values\": [ 1.0, 0.0 ]\n                }\n            ]\n        },\n        {\n            \"program\": \"minecraft:post/entity_outline_box_blur\",\n            \"inputs\": [\n                {\n                    \"sampler_name\": \"In\",\n                    \"target\": \"minecraft:entity_outline\",\n                    \"bilinear\": true\n                }\n            ],\n            \"output\": \"swap\",\n            \"uniforms\": [\n                {\n                    \"name\": \"BlurDir\",\n                    \"values\": [ 0.0, 1.0 ]\n                }\n            ]\n        },\n        {\n            \"program\": \"minecraft:post/entity_outline_composite\",\n            \"inputs\": [\n                {\n                    \"sampler_name\": \"Glow\",\n                    \"target\": \"swap\"\n                },\n                {\n                    \"sampler_name\": \"Original\",\n                    \"target\": \"original\"\n                }\n            ],\n            \"output\": \"minecraft:entity_outline\"\n        }\n    ]\n}\n"
         .getBytes();
   }

   private static byte[] d() {
      return "{\n    \"blend\": {\n        \"func\": \"add\",\n        \"srcrgb\": \"one\",\n        \"dstrgb\": \"zero\"\n    },\n    \"vertex\": \"minecraft:post/blur\",\n    \"fragment\": \"minecraft:post/entity_outline_box_blur\",\n    \"samplers\": [\n        { \"name\": \"InSampler\" }\n    ],\n    \"uniforms\": [\n        { \"name\": \"ProjMat\",          \"type\": \"matrix4x4\", \"count\": 16, \"values\": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ] },\n        { \"name\": \"InSize\",           \"type\": \"float\",     \"count\": 2,  \"values\": [ 1.0, 1.0 ] },\n        { \"name\": \"OutSize\",          \"type\": \"float\",     \"count\": 2,  \"values\": [ 1.0, 1.0 ] },\n        { \"name\": \"BlurDir\",          \"type\": \"float\",     \"count\": 2,  \"values\": [ 1.0, 1.0 ] },\n        { \"name\": \"BlurWeight0\",      \"type\": \"float\",     \"count\": 1,  \"values\": [ 0.20 ] },\n        { \"name\": \"BlurWeight1\",      \"type\": \"float\",     \"count\": 1,  \"values\": [ 0.18 ] },\n        { \"name\": \"BlurWeight2\",      \"type\": \"float\",     \"count\": 1,  \"values\": [ 0.12 ] },\n        { \"name\": \"BlurWeight3\",      \"type\": \"float\",     \"count\": 1,  \"values\": [ 0.05 ] },\n        { \"name\": \"BlurRadius1\",      \"type\": \"float\",     \"count\": 1,  \"values\": [ 2.0 ] },\n        { \"name\": \"BlurRadius2\",      \"type\": \"float\",     \"count\": 1,  \"values\": [ 4.0 ] },\n        { \"name\": \"BlurRadius3\",      \"type\": \"float\",     \"count\": 1,  \"values\": [ 6.0 ] },\n        { \"name\": \"Brightness\",       \"type\": \"float\",     \"count\": 1,  \"values\": [ 1.1 ] }\n    ]\n}\n"
         .getBytes();
   }

   private static byte[] e() {
      String s = "#version 150\n\nuniform sampler2D InSampler;\nuniform float BlurWeight0;\nuniform float BlurWeight1;\nuniform float BlurWeight2;\nuniform float BlurWeight3;\nuniform float BlurRadius1;\nuniform float BlurRadius2;\nuniform float BlurRadius3;\nuniform float Brightness;\n\nin vec2 texCoord;\nin vec2 sampleStep;\n\nout vec4 fragColor;\n\nvoid main() {\n    // Центральный пиксель\n    vec4 result = texture(InSampler, texCoord) * BlurWeight0;\n\n    // Радиусы из uniform\n    vec2 s1 = sampleStep * BlurRadius1;\n    vec2 s2 = sampleStep * BlurRadius2;\n    vec2 s3 = sampleStep * BlurRadius3;\n\n    result += (texture(InSampler, texCoord + s1) + texture(InSampler, texCoord - s1)) * BlurWeight1;\n    result += (texture(InSampler, texCoord + s2) + texture(InSampler, texCoord - s2)) * BlurWeight2;\n    result += (texture(InSampler, texCoord + s3) + texture(InSampler, texCoord - s3)) * BlurWeight3;\n\n    // Усиливаем яркость свечения\n    fragColor = result * Brightness;\n}\n";
      return s.getBytes();
   }

   private static byte[] f() {
      return "#version 150\n\nin vec4 Position;\n\nuniform mat4 ProjMat;\nuniform vec2 InSize;\nuniform vec2 OutSize;\nuniform vec2 BlurDir;\n\nout vec2 texCoord;\nout vec2 sampleStep;\n\nvoid main() {\n    vec4 outPos = ProjMat * vec4(Position.xy, 0.0, 1.0);\n    gl_Position = vec4(outPos.xy, 0.2, 1.0);\n\n    vec2 oneTexel = 1.0 / InSize;\n    sampleStep = oneTexel * BlurDir;\n\n    texCoord = Position.xy / OutSize;\n}\n"
         .getBytes();
   }

   private static byte[] g() {
      return "{\n    \"blend\": {\n        \"func\": \"add\",\n        \"srcrgb\": \"one\",\n        \"dstrgb\": \"zero\"\n    },\n    \"vertex\": \"minecraft:post/blur\",\n    \"fragment\": \"minecraft:post/entity_outline_composite\",\n    \"samplers\": [\n        { \"name\": \"GlowSampler\" },\n        { \"name\": \"OriginalSampler\" }\n    ],\n    \"uniforms\": [\n        { \"name\": \"ProjMat\", \"type\": \"matrix4x4\", \"count\": 16, \"values\": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ] },\n        { \"name\": \"InSize\", \"type\": \"float\", \"count\": 2, \"values\": [ 1.0, 1.0 ] },\n        { \"name\": \"OutSize\", \"type\": \"float\", \"count\": 2, \"values\": [ 1.0, 1.0 ] },\n        { \"name\": \"GlowIntensity\", \"type\": \"float\", \"count\": 1, \"values\": [ 2.2 ] },\n        { \"name\": \"OriginalIntensity\", \"type\": \"float\", \"count\": 1, \"values\": [ 1.1 ] }\n    ]\n}\n"
         .getBytes();
   }

   private static byte[] h() {
      return "#version 150\n\nuniform sampler2D GlowSampler;\nuniform sampler2D OriginalSampler;\nuniform float GlowIntensity;\nuniform float OriginalIntensity;\n\nin vec2 texCoord;\n\nout vec4 fragColor;\n\nvoid main() {\n    vec4 glow = texture(GlowSampler, texCoord);\n    vec4 original = texture(OriginalSampler, texCoord);\n\n    // Яркое свечение + четкая линия поверх (значения через uniforms)\n    vec4 result = glow * GlowIntensity + original * OriginalIntensity;\n\n    fragColor = min(result, vec4(1.0));\n}\n"
         .getBytes();
   }
}
