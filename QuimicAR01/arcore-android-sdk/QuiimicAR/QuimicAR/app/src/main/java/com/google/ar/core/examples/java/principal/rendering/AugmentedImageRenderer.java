package com.google.ar.core.examples.java.principal.rendering;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.common.rendering.ObjectRenderer;
import com.google.ar.core.examples.java.common.rendering.ObjectRenderer.BlendMode;
import java.io.IOException;

public class AugmentedImageRenderer {

    private final ObjectRenderer molecule = new ObjectRenderer();

    // Constantes para configuração
    private static final float TINT_INTENSITY = 0.1f;
    private static final float TINT_ALPHA = 1.0f;
    private static final float SCALE_FACTOR = 0.25f; // Fator de escala para o modelo 3D
    private static final float OFFSET_Y = -0.5f;  // Deslocamento no eixo Y para ajustar posição, atualmente -50cm
    private static final float OFFSET_X = 0.02f;// Deslocamento no eixo x para ajustar posição, atualmente 2cm
    private static final int[] TINT_COLORS_HEX = {
            0x000000, 0xF44336, 0xE91E63, 0x9C27B0, 0x673AB7, 0x3F51B5, 0x2196F3, 0x03A9F4, 0x00BCD4,
            0x009688, 0x4CAF50, 0x8BC34A, 0xCDDC39, 0xFFEB3B, 0xFFC107, 0xFF9800,
    };

    public AugmentedImageRenderer() {}

    // Configura o objeto 3D para renderização no OpenGL
    public void createOnGlThread(Context context) throws IOException {
        try {
            molecule.createOnGlThread(context, "models/metano.obj", "models/metano.png");
            molecule.setMaterialProperties(0.0f, 1.0f, 1.0f, 6.0f);
            molecule.setBlendMode(ObjectRenderer.BlendMode.AlphaBlending);
        } catch (IOException e) {
            Log.e("AugmentedImageRenderer", "Erro ao carregar modelo 3D.", e);
            throw e;
        }
    }

    // Desenha o modelo 3D com base em matrizes e dados fornecidos
    public void draw(
            float[] viewMatrix,
            float[] projectionMatrix,
            AugmentedImage augmentedImage,
            Anchor centerAnchor,
            float[] colorCorrectionRgba) {

        // Determinar cor com base no índice da imagem
        float[] tintColor = convertHexToColor(TINT_COLORS_HEX[augmentedImage.getIndex() % TINT_COLORS_HEX.length]);

        float[] modelMatrix = new float[16];
        float[] translationMatrix = new float[16];
        float[] scaleMatrix = new float[16];

        // Obtém a pose da âncora e cria a matriz inicial do modelo
        Pose anchorPose = centerAnchor.getPose();
        anchorPose.toMatrix(modelMatrix, 0);
        anchorPose.toMatrix(translationMatrix, 0);

        // Define a matriz de translação
        Matrix.setIdentityM(translationMatrix, 0);
        Matrix.translateM(translationMatrix, 0, OFFSET_X, OFFSET_Y, 0.0f);
        Matrix.multiplyMM(modelMatrix, 0, translationMatrix, 0, modelMatrix, 0);

        // Define a matriz de escala
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, SCALE_FACTOR, SCALE_FACTOR, SCALE_FACTOR);
        Matrix.multiplyMM(modelMatrix, 0, scaleMatrix, 0, modelMatrix, 0);

        // Atualizar e desenhar o modelo
        molecule.updateModelMatrix(modelMatrix, SCALE_FACTOR);

        // Desenha o modelo com as matrizes configuradas
        molecule.draw(viewMatrix, projectionMatrix, colorCorrectionRgba, tintColor);
    }

    // Converte uma cor hexadecimal para um vetor RGBA (usado no OpenGL)
    private static float[] convertHexToColor(int colorHex) {
        float red = ((colorHex & 0xFF0000) >> 16) / 255.0f * TINT_INTENSITY;
        float green = ((colorHex & 0x00FF00) >> 8) / 255.0f * TINT_INTENSITY;
        float blue = (colorHex & 0x0000FF) / 255.0f * TINT_INTENSITY;
        return new float[]{red, green, blue, TINT_ALPHA};
    }
}
