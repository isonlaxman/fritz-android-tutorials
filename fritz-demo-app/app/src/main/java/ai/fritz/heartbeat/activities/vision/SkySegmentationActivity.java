package ai.fritz.heartbeat.activities.vision;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.Size;

import java.util.ArrayList;
import java.util.List;

import ai.fritz.fritzvisionhairsegmentationmodel.HairSegmentationOnDeviceModel;
import ai.fritz.heartbeat.activities.BaseLiveVideoActivity;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.imagesegmentation.BlendMode;
import ai.fritz.vision.imagesegmentation.BlendModeType;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentPredictorOptions;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentResult;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentTFLPredictor;
import ai.fritz.vision.imagesegmentation.MaskType;
import ai.fritz.vision.imagesegmentation.SegmentOnDeviceModel;

import ai.fritz.fritzvisionoutdoorsegmentation.OutdoorSegmentOnDeviceModel;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentPredictor;

public class SkySegmentationActivity extends BaseLiveVideoActivity {
    private static final int maskColor = Color.RED;

    private static final BlendMode blendMode = BlendModeType.HUE.create();
    private FritzVisionSegmentPredictor skyPredictor;
    private FritzVisionSegmentResult skyResult;

    @Override
    protected void onCameraSetup(final Size cameraSize) {
        SegmentOnDeviceModel outdoorSegmentOnDeviceModel = new OutdoorSegmentOnDeviceModel();

        // List the segments to target
        List<MaskType> targetMasks = new ArrayList<>();
        targetMasks.add(MaskType.SKY);

        // Create predictor options with a confidence threshold.
        // If it's below the confidence threshold, the segment will be marked
        // as SegmentClass.NONE.
        FritzVisionSegmentPredictorOptions options = new FritzVisionSegmentPredictorOptions.Builder()
                .targetSegmentClasses(targetMasks)
                .targetConfidenceThreshold(.3f)
                .build();

        skyPredictor = FritzVision.ImageSegmentation.getPredictor(outdoorSegmentOnDeviceModel, options);
    }

    @Override
    protected void handleDrawingResult(Canvas canvas, Size cameraSize) {
        if (skyResult != null) {
            FritzVisionImage originalImage = skyResult.getOriginalImage();
            Bitmap maskBitmap = skyResult.createMaskOverlayBitmap(blendMode.getAlpha());
            Bitmap blendedBitmap = originalImage.blend(maskBitmap, blendMode);
            canvas.drawBitmap(blendedBitmap, null, new RectF(0, 0, cameraSize.getWidth(), cameraSize.getHeight()), null);
        }
    }

    @Override
    protected void runInference(FritzVisionImage fritzVisionImage) {
        skyResult = skyPredictor.predict(fritzVisionImage);
    }
}
