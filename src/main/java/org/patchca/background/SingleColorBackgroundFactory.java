/*
 * Copyright (c) 2009 Piotr Piastucki
 * 
 * This file is part of Patchca CAPTCHA library.
 * 
 *  Patchca is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Patchca is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Patchca. If not, see <http://www.gnu.org/licenses/>.
 */
package org.patchca.background;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.patchca.color.ColorFactory;
import org.patchca.color.SingleColorFactory;

public class SingleColorBackgroundFactory implements BackgroundFactory {

	private ColorFactory colorFactory;

	public SingleColorBackgroundFactory() {
		colorFactory = new SingleColorFactory(Color.WHITE);
	}

	public SingleColorBackgroundFactory(Color color) {
		colorFactory = new SingleColorFactory(color);
	}

	public void setColorFactory(ColorFactory colorFactory) {
		this.colorFactory = colorFactory;
	}

	@Override
	public BufferedImage fillBackground(int width, int height) {
		BufferedImage bufImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bufImage.getGraphics();
		g.setColor(colorFactory.getColor(0));
		g.fillRect(0, 0, bufImage.getWidth(), bufImage.getHeight());
		g.dispose();
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
