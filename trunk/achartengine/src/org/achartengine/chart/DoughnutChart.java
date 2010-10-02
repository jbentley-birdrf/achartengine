/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.achartengine.chart;

import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

/**
 * The doughnut chart rendering class.
 */
public class DoughnutChart extends AbstractChart {
  /** The legend shape width. */
  private static final int SHAPE_WIDTH = 10;

  /** The series dataset. */
  private MultipleCategorySeries mDataset;

  /** The series renderer. */
  private DefaultRenderer mRenderer;

  /** A step variable to control the size of the legend shape. */
  private int mStep;

  /**
   * Builds a new pie chart instance.
   * 
   * @param dataset the series dataset
   * @param renderer the series renderer
   */
  public DoughnutChart(MultipleCategorySeries dataset, DefaultRenderer renderer) {
    mDataset = dataset;
    mRenderer = renderer;
  }

  /**
   * The graphical representation of the pie chart.
   * 
   * @param canvas the canvas to paint to
   * @param x the top left x value of the view to draw to
   * @param y the top left y value of the view to draw to
   * @param width the width of the view to draw to
   * @param height the height of the view to draw to
   */
  @Override
  public void draw(Canvas canvas, int x, int y, int width, int height) {
    Paint paint = new Paint();
    paint.setAntiAlias(mRenderer.isAntialiasing());
    paint.setStyle(Style.FILL);
    paint.setTextSize(mRenderer.getLabelsTextSize());
    int legendSize = 30;
    if (mRenderer.isShowLegend()) {
      legendSize = height / 5;
    }
    int left = x + 15;
    int top = y + 5;
    int right = x + width - 5;
    int bottom = y + height - legendSize;
    drawBackground(mRenderer, canvas, x, y, width, height, paint);
    mStep = SHAPE_WIDTH * 3 / 4;

    int cLength = mDataset.getCategoriesCount();
    int mRadius = Math.min(Math.abs(right - left), Math.abs(bottom - top));
    double rCoef = 0.35;
    double decCoef = 0.2 / cLength;
    int radius = (int) (mRadius * rCoef);
    int centerX = (left + right) / 2;
    int centerY = (bottom + top) / 2;
    float shortRadius = radius * 0.9f;
    float longRadius = radius * 1.1f;
    String[] categories = new String[cLength];
    for (int category = 0; category < cLength; category++) {
      int sLength = mDataset.getItemCount(category);
      double total = 0;
      String[] titles = new String[sLength];
      for (int i = 0; i < sLength; i++) {
        total += mDataset.getValues(category)[i];
        titles[i] = mDataset.getTitles(category)[i];
      }
      float currentAngle = 0;
      RectF oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
      for (int i = 0; i < sLength; i++) {
        paint.setColor(mRenderer.getSeriesRendererAt(i).getColor());
        float value = (float) mDataset.getValues(category)[i];
        float angle = (float) (value / total * 360);
        canvas.drawArc(oval, currentAngle, angle, true, paint);
        if (mRenderer.isShowLabels()) {
          paint.setColor(mRenderer.getLabelsColor());
          double rAngle = Math.toRadians(90 - (currentAngle + angle / 2));
          double sinValue = Math.sin(rAngle);
          double cosValue = Math.cos(rAngle);
          int x1 = Math.round(centerX + (float) (shortRadius * sinValue));
          int y1 = Math.round(centerY + (float) (shortRadius * cosValue));
          int x2 = Math.round(centerX + (float) (longRadius * sinValue));
          int y2 = Math.round(centerY + (float) (longRadius * cosValue));
          canvas.drawLine(x1, y1, x2, y2, paint);
          int extra = 10;
          paint.setTextAlign(Align.LEFT);
          if (x1 > x2) {
            extra = -extra;
            paint.setTextAlign(Align.RIGHT);
          }
          canvas.drawLine(x2, y2, x2 + extra, y2, paint);
          canvas.drawText(mDataset.getTitles(category)[i], x2 + extra, y2 + 5, paint);
        }
        currentAngle += angle;
      }
      radius -= (int) mRadius * decCoef;
      shortRadius -= mRadius * decCoef - 2;
      if (mRenderer.getBackgroundColor() != 0) {
        paint.setColor(mRenderer.getBackgroundColor());
      } else {
        paint.setColor(Color.WHITE);
      }
      paint.setStyle(Style.FILL);
      oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
      canvas.drawArc(oval, 0, 360, true, paint);
      radius -= 1;
      categories[category] = mDataset.getCategory(category);
    }
    drawLegend(canvas, mRenderer, categories, left, right, y, width, height, legendSize, paint);
  }

  /**
   * Returns the legend shape width.
   * 
   * @return the legend shape width
   */
  public int getLegendShapeWidth() {
    return SHAPE_WIDTH;
  }

  /**
   * The graphical representation of the legend shape.
   * 
   * @param canvas the canvas to paint to
   * @param renderer the series renderer
   * @param x the x value of the point the shape should be drawn at
   * @param y the y value of the point the shape should be drawn at
   * @param paint the paint to be used for drawing
   */
  public void drawLegendShape(Canvas canvas, SimpleSeriesRenderer renderer, float x, float y,
      Paint paint) {
    mStep--;
    canvas.drawCircle(x + SHAPE_WIDTH - mStep, y, mStep, paint);
  }

}