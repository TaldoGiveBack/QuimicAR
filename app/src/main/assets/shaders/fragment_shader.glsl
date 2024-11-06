precision mediump float;

varying vec3 v_Normal;
varying vec2 v_TexCoord;
varying vec4 v_Color; // Nova variável para receber a cor de cada átomo.

uniform sampler2D u_Texture;

void main() {
    vec3 color = v_Color.rgb; // Definindo a cor com base na variável que será enviada.
    gl_FragColor = vec4(color, 1.0); // Definindo a cor final.
}
