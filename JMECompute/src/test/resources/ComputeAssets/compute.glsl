
layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

#import "Common/ShaderLib/GLSLCompat.glsllib"

layout (RGBA8) uniform image2D TargetImage;

void main() {

    ivec2 texel = ivec2(gl_GlobalInvocationID.xy);
    vec4 color = vec4(vec2(texel) / (gl_NumWorkGroups.xy * gl_WorkGroupSize.xy), 0.0, 1.0);
    imageStore(TargetImage, texel, color);

}
