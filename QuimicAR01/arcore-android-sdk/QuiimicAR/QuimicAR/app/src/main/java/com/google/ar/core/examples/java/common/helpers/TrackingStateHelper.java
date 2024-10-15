/*
 * Copyright 2019 Google LLC
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
package com.google.ar.core.examples.java.common.helpers;

import android.app.Activity;
import android.view.WindowManager;
import com.google.ar.core.Camera;
import com.google.ar.core.TrackingFailureReason;
import com.google.ar.core.TrackingState;

/** Gets human readibly tracking failure reasons and suggested actions. */
public final class TrackingStateHelper {
  private static final String INSUFFICIENT_FEATURES_MESSAGE =
      "Nada foi detectado. Aponte o dispositivo para uma superfície com mais cor ou textura.";
  private static final String EXCESSIVE_MOTION_MESSAGE = "Muito rápido. Mova-se mais devagar";
  private static final String INSUFFICIENT_LIGHT_MESSAGE =
      "Muito escuro. Tente novamente em uma área mais bem-iluminada.";
  private static final String INSUFFICIENT_LIGHT_ANDROID_S_MESSAGE =
          "Muito escuro. Tente novamente em uma área mais bem-iluminada."
      + " Além disso, certifique-se que o Bloquear Câmera esteja desativado nas configurações do sistema.";
  private static final String BAD_STATE_MESSAGE =
      "Rastreamento perdido por problemas internos. Tente reiniciar o aplicativo.";
  private static final String CAMERA_UNAVAILABLE_MESSAGE =
      "Outro aplicativo está usando a câmera. Selecione este aplicativo ou feche o outro.";
  private static final int ANDROID_S_SDK_VERSION = 31;

  private final Activity activity;

  private TrackingState previousTrackingState;

  public TrackingStateHelper(Activity activity) {
    this.activity = activity;
  }

  /** Keep the screen unlocked while tracking, but allow it to lock when tracking stops. */
  public void updateKeepScreenOnFlag(TrackingState trackingState) {
    if (trackingState == previousTrackingState) {
      return;
    }

    previousTrackingState = trackingState;
    switch (trackingState) {
      case PAUSED:
      case STOPPED:
        activity.runOnUiThread(
            () -> activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON));
        break;
      case TRACKING:
        activity.runOnUiThread(
            () -> activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON));
        break;
    }
  }

  public static String getTrackingFailureReasonString(Camera camera) {
    TrackingFailureReason reason = camera.getTrackingFailureReason();
    switch (reason) {
      case NONE:
        return "";
      case BAD_STATE:
        return BAD_STATE_MESSAGE;
      case INSUFFICIENT_LIGHT:
        if (android.os.Build.VERSION.SDK_INT < ANDROID_S_SDK_VERSION) {
            return INSUFFICIENT_LIGHT_MESSAGE;
        } else {
            return INSUFFICIENT_LIGHT_ANDROID_S_MESSAGE;
        }
      case EXCESSIVE_MOTION:
        return EXCESSIVE_MOTION_MESSAGE;
      case INSUFFICIENT_FEATURES:
        return INSUFFICIENT_FEATURES_MESSAGE;
      case CAMERA_UNAVAILABLE:
        return CAMERA_UNAVAILABLE_MESSAGE;
    }
    return "Falha de rastreamento desconhecida: " + reason;
  }
}
