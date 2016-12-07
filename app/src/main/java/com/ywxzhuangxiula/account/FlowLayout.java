package com.ywxzhuangxiula.account;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FlowLayout extends ViewGroup {

	private List<ChildPos> mChildPos = new ArrayList<ChildPos>();

	private class ChildPos {
		int left, top, right, bottom;

		public ChildPos(int left, int top, int right, int bottom) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}
	}

	public FlowLayout(Context context) {
		this(context, null);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}


	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);

		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);


		int width = 0, height = 0;

		int lineWidth = 0, lineHeight = 0;

		int count = getChildCount();
		mChildPos.clear();
		for (int i = 0; i < count; i++) {

			View child = getChildAt(i);

			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

			int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;

			int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

			if (lineWidth + childWidth > widthSize - getPaddingLeft() - getPaddingRight()) {

				width = Math.max(width, lineWidth);

				height += lineHeight;

				lineWidth = childWidth;

				lineHeight = childHeight;

				mChildPos.add(new ChildPos(getPaddingLeft() + lp.leftMargin, getPaddingTop() + height + lp.topMargin,
						getPaddingLeft() + childWidth - lp.rightMargin,
						getPaddingTop() + height + childHeight - lp.bottomMargin));
			} else {

				mChildPos.add(new ChildPos(getPaddingLeft() + lineWidth + lp.leftMargin,
						getPaddingTop() + height + lp.topMargin,
						getPaddingLeft() + lineWidth + childWidth - lp.rightMargin,
						getPaddingTop() + height + childHeight - lp.bottomMargin));

				lineWidth += childWidth;

				lineHeight = Math.max(lineHeight, childHeight);
			}

			if (i == count - 1) {
				width = Math.max(lineWidth, width);
				height += lineHeight;
			}
		}


		setMeasuredDimension(
				widthMode == MeasureSpec.AT_MOST ? width + getPaddingLeft() + getPaddingRight() : widthSize,
				heightMode == MeasureSpec.AT_MOST ? height + getPaddingTop() + getPaddingBottom() : heightSize);
	}


	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			ChildPos pos = mChildPos.get(i);

			child.layout(pos.left, pos.top, pos.right, pos.bottom);
		}
	}
}