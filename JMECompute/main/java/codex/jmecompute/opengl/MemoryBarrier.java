/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package codex.jmecompute.opengl;

import org.lwjgl.opengl.GL43;

/**
 *
 * @author codex
 */
public class MemoryBarrier {
    
    public static final MemoryBarrier
            All = new MemoryBarrier(GL43.GL_ALL_BARRIER_BITS),
            VertexAttributeArray = new MemoryBarrier(GL43.GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT),
            ElementArray = new MemoryBarrier(GL43.GL_ELEMENT_ARRAY_BARRIER_BIT),
            Uniform = new MemoryBarrier(GL43.GL_UNIFORM_BARRIER_BIT),
            TextureFetch = new MemoryBarrier(GL43.GL_TEXTURE_FETCH_BARRIER_BIT),
            ShaderImageAccess = new MemoryBarrier(GL43.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT),
            CommandBarrier = new MemoryBarrier(GL43.GL_COMMAND_BARRIER_BIT),
            PixelBuffer = new MemoryBarrier(GL43.GL_PIXEL_BUFFER_BARRIER_BIT),
            TextureUpdate = new MemoryBarrier(GL43.GL_TEXTURE_UPDATE_BARRIER_BIT),
            BufferUpdate = new MemoryBarrier(GL43.GL_BUFFER_UPDATE_BARRIER_BIT),
            Framebuffer = new MemoryBarrier(GL43.GL_FRAMEBUFFER_BARRIER_BIT),
            TransformFeedback = new MemoryBarrier(GL43.GL_TRANSFORM_FEEDBACK_BARRIER_BIT),
            AtomicCounter = new MemoryBarrier(GL43.GL_ATOMIC_COUNTER_BARRIER_BIT),
            ShaderStorage = new MemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
    
    private int barrier;

    private MemoryBarrier(int barrier) {
        this.barrier = barrier;
    }

    public int getBarrier() {
        return barrier;
    }
    
    public MemoryBarrier combine(MemoryBarrier... barriers) {
        MemoryBarrier result = new MemoryBarrier(0);
        for (MemoryBarrier b : barriers) {
            result.barrier |= b.barrier;
        }
        return result;
    }
    
}
