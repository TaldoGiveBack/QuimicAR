/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.core.examples.java.principal.rendering;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.common.rendering.ObjectRenderer;
import com.google.ar.core.examples.java.common.rendering.ObjectRenderer.BlendMode;
import java.io.IOException;

/** Renders an augmented image. */
public class AugmentedImageRenderer {


  private final ObjectRenderer molecule = new ObjectRenderer();


  public AugmentedImageRenderer() {}

  private static final float TINT_INTENSITY = 0.1f;
  private static final float TINT_ALPHA = 1.0f;
  private static final int[] TINT_COLORS_HEX = {
          0x000000, 0xF44336, 0xE91E63, 0x9C27B0, 0x673AB7, 0x3F51B5, 0x2196F3, 0x03A9F4, 0x00BCD4,
          0x009688, 0x4CAF50, 0x8BC34A, 0xCDDC39, 0xFFEB3B, 0xFFC107, 0xFF9800,
  };

  public void createOnGlThread(Context context) throws IOException {

    molecule.createOnGlThread(
        context, "models/metano.obj", "models/metano.png");
    molecule.setMaterialProperties(0.0f, 1.0f, 1.0f, 6.0f);
    molecule.setBlendMode(BlendMode.AlphaBlending);

  }

  public void draw(
          float[] viewMatrix,
          float[] projectionMatrix,
          AugmentedImage augmentedImage,
          Anchor centerAnchor,
          float[] colorCorrectionRgba) {

    // Convertendo o índice da imagem para uma cor de tintura
    float[] tintColor = convertHexToColor(TINT_COLORS_HEX[augmentedImage.getIndex() % TINT_COLORS_HEX.length]);

    // Fator de escala do objeto 3D
    float scaleFactor = 0.25f;  // Você pode ajustar esse valor dependendo do tamanho da imagem e do modelo

    // Obtendo a posição da âncora da imagem no espaço 3D
    Pose anchorPose = centerAnchor.getPose();

    // Pegando as dimensões da imagem aumentada no mundo real
    float augmentedImageWidth = augmentedImage.getExtentX();
    float augmentedImageHeight = augmentedImage.getExtentZ();

    // Criando uma matriz de transformação para o objeto 3D
    float[] modelMatrix = new float[16];
    anchorPose.toMatrix(modelMatrix, 0);

    // Criando uma matriz de translação para mover o objeto
    float[] translationMatrix = new float[16];
    android.opengl.Matrix.setIdentityM(translationMatrix, 0);

    // Ajustando a posição para centralizar o objeto na imagem aumentada
    // Vamos mover o objeto para o centro da imagem no espaço 3D

    // A posição do centro da imagem aumentada no eixo X e Z
    // Para centralizar o objeto, vamos mover metade da largura e da altura
    float translationX = 0.0f; // Para centralizar no eixo X
    float translationZ = 0.0f; // Para centralizar no eixo Z
    float offsetY = -0.4f;     // Ajuste no eixo Y, se necessário

    // Aplica a translação (estamos utilizando 0.0f para X e Z para centralizar o objeto)
    android.opengl.Matrix.translateM(translationMatrix, 0, translationX, offsetY, translationZ);

    // Multiplica a matriz de translação com a matriz do modelo
    android.opengl.Matrix.multiplyMM(modelMatrix, 0, translationMatrix, 0, modelMatrix, 0);

    // Criando uma matriz de escala para o objeto 3D
    float[] scaleMatrix = new float[16];
    android.opengl.Matrix.setIdentityM(scaleMatrix, 0);
    android.opengl.Matrix.scaleM(scaleMatrix, 0, scaleFactor, scaleFactor, scaleFactor);

    // Multiplica a matriz de escala com a matriz do modelo
    android.opengl.Matrix.multiplyMM(modelMatrix, 0, scaleMatrix, 0, modelMatrix, 0);

    // Atualiza a matriz do modelo com a transformação calculada
    molecule.updateModelMatrix(modelMatrix, scaleFactor);

    // Desenha o objeto 3D
    molecule.draw(viewMatrix, projectionMatrix, colorCorrectionRgba, tintColor);
  }




  private static float[] convertHexToColor(int colorHex) {
    // colorHex is in 0xRRGGBB format
    float red = ((colorHex & 0xFF0000) >> 16) / 255.0f * TINT_INTENSITY;
    float green = ((colorHex & 0x00FF00) >> 8) / 255.0f * TINT_INTENSITY;
    float blue = (colorHex & 0x0000FF) / 255.0f * TINT_INTENSITY;
    return new float[] {red, green, blue, TINT_ALPHA};
  }

}
