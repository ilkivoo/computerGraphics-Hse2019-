package ru.hse.alyokhina;

import com.jogamp.opengl.GL2;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import kotlin.io.TextStreamsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;


public final class Shader {
    private final int programId;
    private final int fragmentShaderId;
    private final int vertexShaderId;
    private final GL2 gl;

    private Pair<Integer, Integer> setUpShader(String fragmentShader, String vertexShader) throws Exception {
        URL fragmentShaderUrl = this.getClass().getClassLoader().getResource(fragmentShader);
        assert fragmentShaderUrl != null;
        String fragmentShaderCode = new String(TextStreamsKt.readBytes(fragmentShaderUrl), Charsets.UTF_8);
        URL vertexShaderUrl = this.getClass().getClassLoader().getResource(vertexShader);
        assert vertexShaderUrl != null;
        String vertexShaderCode = new String(TextStreamsKt.readBytes(vertexShaderUrl), Charsets.UTF_8);
        int fragmentShaderId = this.createShaderProgram(fragmentShaderCode, 35632);
        int vertexShaderId = this.createShaderProgram(vertexShaderCode, 35633);
        this.gl.glLinkProgram(this.programId);
        this.checkProgramErrors(35714);
        this.gl.glValidateProgram(this.programId);
        this.checkProgramErrors(35715);
        this.gl.glUseProgram(this.programId);
        return new Pair<>(fragmentShaderId, vertexShaderId);
    }

    private int createShaderProgram(String shaderCode, int shaderType) throws Exception {
        int shaderId = this.gl.glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Unable to create shader");
        } else {
            this.gl.glShaderSource(shaderId, 1, new String[]{shaderCode}, null);
            this.gl.glCompileShader(shaderId);
            this.checkShaderErrors(shaderId, 35713);
            this.gl.glAttachShader(this.programId, shaderId);
            return shaderId;
        }
    }

    private void checkShaderErrors(int shaderId, int statusToCheck) throws Exception {
        IntBuffer intBuffer = IntBuffer.allocate(1);
        this.gl.glGetShaderiv(shaderId, statusToCheck, intBuffer);
        if (intBuffer.get(0) != 1) {
            this.gl.glGetShaderiv(shaderId, 35716, intBuffer);
            int size = intBuffer.get(0);
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            this.gl.glGetShaderInfoLog(shaderId, size, intBuffer, byteBuffer);
            throw new Exception("Shader error: " + new String(byteBuffer.array()));
        }
    }

    private void checkProgramErrors(int statusToCheck) throws Exception {
        IntBuffer intBuffer = IntBuffer.allocate(1);
        this.gl.glGetProgramiv(this.programId, statusToCheck, intBuffer);
        if (intBuffer.get(0) != 1) {
            this.gl.glGetProgramiv(this.programId, GL2.GL_INFO_LOG_LENGTH, intBuffer);
            int size = intBuffer.get(0);
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            this.gl.glGetProgramInfoLog(this.programId, size, intBuffer, byteBuffer);
            throw new Exception("Program  error: " + new String(byteBuffer.array()));
        }
    }

    public final void delete() {
        this.gl.glDetachShader(this.programId, this.vertexShaderId);
        this.gl.glDetachShader(this.programId, this.fragmentShaderId);
        this.gl.glDeleteProgram(this.programId);
    }

    public final void setValue(String variable, float value) {
        Intrinsics.checkParameterIsNotNull(variable, "variable");
        int location = this.gl.glGetUniformLocation(this.programId, variable);
        this.gl.glUniform1f(location, value);
    }

    public final void setValue(String variable, int value) {
        Intrinsics.checkParameterIsNotNull(variable, "variable");
        int location = this.gl.glGetUniformLocation(this.programId, variable);
        this.gl.glUniform1i(location, value);
    }

    public final void setValue(String variable, float value1, float value2) {
        Intrinsics.checkParameterIsNotNull(variable, "variable");
        int location = this.gl.glGetUniformLocation(this.programId, variable);
        this.gl.glUniform2f(location, value1, value2);
    }

    public Shader(GL2 gl, String fragmentShader, String vertexShader) throws Exception {
        this.gl = gl;
        this.programId = this.gl.glCreateProgram();
        Pair<Integer, Integer> pair = this.setUpShader(fragmentShader, vertexShader);
        this.fragmentShaderId = pair.getFirst();
        this.vertexShaderId = pair.getFirst();
    }
}
