/*
 * Copyright 2010 Daniel Kurka
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.mgwt.ui.client.widget;

import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.theme.base.SliderCss;
import com.googlecode.mgwt.ui.client.util.CssUtil;
import com.googlecode.mgwt.ui.client.widget.touch.TouchWidget;

/**
 * @author Daniel Kurka
 * 
 */
public class MSlider extends Composite implements HasValue<Integer>, LeafValueEditor<Integer> {

	private static class SliderWidget extends TouchWidget {

		private Element slider;
		private Element bar;

		public SliderWidget(SliderCss css) {
			setElement(DOM.createDiv());
			bar = DOM.createDiv();
			bar.setClassName(css.bar());

			slider = DOM.createDiv();
			slider.setClassName(css.pointer());
			bar.appendChild(slider);

			getElement().appendChild(bar);

		}

		public void setPos(int x) {
			CssUtil.translate(slider, x, 0);
		}
	}

	private SliderWidget sliderWidget;

	private class SliderTouchHandler implements TouchHandler {

		@Override
		public void onTouchStart(TouchStartEvent event) {
			setValueContrained(event.touches().get(0).getPageX());

		}

		@Override
		public void onTouchMove(TouchMoveEvent event) {

			setValueContrained(event.touches().get(0).getPageX());
		}

		@Override
		public void onTouchEnd(TouchEndEvent event) {

		}

		@Override
		public void onTouchCanceled(TouchCancelEvent event) {

		}

	}

	private int value;

	public MSlider() {
		this(MGWTStyle.getDefaultClientBundle().getSliderCss());
	}

	public MSlider(SliderCss css) {
		css.ensureInjected();
		sliderWidget = new SliderWidget(css);
		initWidget(sliderWidget);
		setStylePrimaryName(css.slider());

		sliderWidget.addTouchHandler(new SliderTouchHandler());

		max = 100;
		value = 0;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	private int max;

	public void setMax(int max) {
		if (max <= 0) {
			throw new IllegalArgumentException("max > 0");
		}
		this.max = max;
	}

	/**
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	private void setValueContrained(int x) {
		x = x - MSlider.this.getAbsoluteLeft();
		int width = sliderWidget.getOffsetWidth();

		if (x < 0) {
			x = 0;
		}

		if (x > width) {
			x = width;
		}

		// scale it to max
		x = x * max / width;
		setValue(x, true);
	}

	@Override
	public Integer getValue() {
		return value;
		//return sliderPos * max / (sliderWidget.getOffsetWidth());
	}

	@Override
	public void setValue(Integer value) {
		setValue(value, true);

	}

	@Override
	protected void onAttach() {
		super.onAttach();
		setSliderPos(value);
	}

	@Override
	public void setValue(Integer value, boolean fireEvents) {
		if (value == null) {
			throw new IllegalArgumentException("value can not be null");
		}

		if (value > max) {
			throw new IllegalArgumentException("value > max");
		}

		int oldValue = this.value;

		setSliderPos(value);

		if (fireEvents) {
			ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
		}

	}

	private void setSliderPos(int value) {

		if (!isAttached()) {
			return;
		}

		int width = sliderWidget.getOffsetWidth();
		int sliderPos = value * width / max;
		sliderWidget.setPos(sliderPos);

	}
}
