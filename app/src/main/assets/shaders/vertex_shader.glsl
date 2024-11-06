attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_TexCoord;
attribute vec4 a_Color; // Nova variável para a cor.

varying vec3 v_Normal;
varying vec2 v_TexCoord;
varying vec4 v_Color; // Passando a cor para o fragment shader.

uniform mat4 u_ModelViewProjection;
uniform mat4 u_ModelView;

void main() {
    v_TexCoord = a_TexCoord;
    v_Normal = mat3(u_ModelView) * a_Normal;
    v_Color = a_Color; // Atribuindo a cor para ser usada no fragment shader.
    gl_Position = u_ModelViewProjection * a_Position;
}
