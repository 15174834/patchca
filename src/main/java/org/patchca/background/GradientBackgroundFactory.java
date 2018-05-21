package org.patchca.background;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

public class GradientBackgroundFactory implements BackgroundFactory {

	public enum Direction {
		Horizontal, Vertical, TopLeftBottomRight, BottomLeftTopRight
	}

	private Color startColor;
	private Color endColor;
	private Direction direction;

	public GradientBackgroundFactory() {
		this(new Color(192, 192, 0), new Color(192, 128, 128), Direction.Horizontal);
	}

	public GradientBackgroundFactory(Color startColor, Color endColor, Direction direction) {
		this.startColor = startColor;
		this.endColor = endColor;
		this.direction = direction;
	}

	private void fillBackground(BufferedImage bufImage) {

		float x1, y1, x2, y2;

		switch (direction) {
		default:
		case Horizontal:
			x1 = 0;
			y1 = 0;
			x2 = bufImage.getWidth();
			y2 = 0;
			break;
		case Vertical:
			x1 = 0;
			y1 = 0;
			x2 = 0;
			y2 = bufImage.getHeight();
			break;
		case BottomLeftTopRight:
			x1 = 0;
			y1 = bufImage.getHeight();
			x2 = bufImage.getWidth();
			y2 = 0;
			break;
		case TopLeftBottomRight:
			x1 = 0;
			y1 = 0;
			x2 = bufImage.getWidth();
			y2 = bufImage.getHeight();
			break;
		}

		GradientPaint gp = new GradientPaint(x1, y1, startColor, x2, y2, endColor);

		Graphics2D g = bufImage.createGraphics();
		g.setPaint(gp);
		g.fillRect(0, 0, bufImage.getWidth(), bufImage.getHeight());
		g.dispose();
	}

	@Override
	public BufferedImage fillBackground(int width, int height) {
		BufferedImage bufImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		fillBackground(bufImage);
		return bufImage;
	}

	@Override
	public BufferedImage transparentBackground(int width, int height) {
		BufferedImage bufImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufImage.createGraphics();
		bufImage = g2d.getDeviceConfiguration().createCompatibleImage(bufImage.getWidth(), bufImage.getHeight(), Transparency.TRANSLUCENT);
		return bufImage;
	}

}