/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package surfacesandregions;

import coordinates.AffineUtils;
import coordinates.XYStagePosition;
import gui.SettingsDialog;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.micromanager.MMStudio;
import org.micromanager.utils.ReportingUtils;

/**
 *
 * @author Henry
 */
public class MultiPosGrid implements XYFootprint{

   private Point2D.Double center_; //stored in stage space
   private int overlapX_, overlapY_, rows_, cols_;
   private RegionManager manager_;
   private String name_;
   private String pixelSizeConfig_;

   public MultiPosGrid(RegionManager manager, int r, int c, Point2D.Double center)  {
      manager_ = manager;
      name_ = manager.getNewName();
      center_ = center;
      try {
         pixelSizeConfig_ = MMStudio.getInstance().getCore().getCurrentPixelSizeConfig();
      } catch (Exception ex) {
         ReportingUtils.showError("couldnt get pixel size config");
      }
      updateParams(r, c);
   }

   public String getName() {
      return name_;
   }

   public void rename(String newName) {
      name_ = newName;
      manager_.updateRegionTableAndCombos();
   }

   public double getWidth_um() {
      double pixelSize = MMStudio.getInstance().getCore().getPixelSizeUm();
      int imageWidth = (int) MMStudio.getInstance().getCore().getImageWidth();
      int pixelWidth = cols_ * (imageWidth - overlapX_) + overlapX_;
      return pixelSize * pixelWidth;
   }

   public double getHeight_um() {
      double pixelSize = MMStudio.getInstance().getCore().getPixelSizeUm();
      int imageHeight = (int) MMStudio.getInstance().getCore().getImageHeight();
      int pixelHeight = rows_ * (imageHeight - overlapY_) + overlapY_;
      return pixelSize * pixelHeight;
   }

   public void updateParams(int rows, int cols) {
      overlapX_ = SettingsDialog.getOverlapX();
      overlapY_ = SettingsDialog.getOverlapY();
      rows_ = rows;
      cols_ = cols;
      manager_.updateRegionTableAndCombos();
      manager_.drawRegionOverlay(this);
   }

   public ArrayList<XYStagePosition> getXYPositions() {
      try {
         AffineTransform transform = AffineUtils.getAffineTransform(MMStudio.getInstance().getCore().getCurrentPixelSizeConfig(), center_.x, center_.y);
         ArrayList<XYStagePosition> positions = new ArrayList<XYStagePosition>();
         int tileWidth = (int) MMStudio.getInstance().getCore().getImageWidth() - SettingsDialog.getOverlapX();
         int tileHeight = (int) MMStudio.getInstance().getCore().getImageHeight() - SettingsDialog.getOverlapY();
         for (int col = 0; col < cols_; col++) {
            double xPixelOffset = (col - (cols_ - 1) / 2.0) * tileWidth;
            for (int row = 0; row < rows_; row++) {
               double yPixelOffset = (row - (rows_ - 1) / 2.0) * tileHeight;
               Point2D.Double pixelPos = new Point2D.Double(xPixelOffset, yPixelOffset);
               Point2D.Double stagePos = new Point2D.Double();
               transform.transform(pixelPos, stagePos);
               positions.add(new XYStagePosition(stagePos, tileWidth, tileHeight, row, col, pixelSizeConfig_));
            }
         }
         return positions;
      } catch (Exception ex) {
         ReportingUtils.showError("Couldn't get affine transform");
         throw new RuntimeException();
      }
   }

   public int numCols() {
      return cols_;
   }

   public int numRows() {
      return rows_;
   }

   public int overlapX() {
      return overlapX_;
   }

   public int overlapY() {
      return overlapY_;
   }

   public Point2D.Double center() {
      return center_;
   }

   public void translate(double dx, double dy) {
      center_ = new Point2D.Double(center_.x + dx, center_.y + dy);
      manager_.drawRegionOverlay(this);
   }
}
