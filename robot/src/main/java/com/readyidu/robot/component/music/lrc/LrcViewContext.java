package com.readyidu.robot.component.music.lrc;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

import com.readyidu.robot.R;
import com.readyidu.robot.utils.view.MeasureUtil;

/**
 * @author Wlq
 * @description 歌词布局
 * @date 2018/2/1 下午8:39
 */
public class LrcViewContext {

	public Context context;
	public Mode CurrentMode = Mode.normal;

	public int LinePaddings = 5;// in px
	public int NormalRowTextSize = 27;// in px
	public int HightlightRowTextSize = 27;// in px
	public int TrySelectRowTextSize = 27;// in px
	public int MessagePaintTextSize = 27;// in px
	public int TimeTextSize = 27;// in px

	public Paint NormalRowPaint;// 正常行画笔
	public Paint HightlightRowPaint;// 当前高亮行画笔
	public Paint TrySelectRowPaint;// 滑动时将要选择的行画笔
	public Paint MessagePaint;// 滑动时将要选择的行画笔
	public Paint SelectLinePaint;
	public Paint TimeTextPaint;

	public LrcViewContext(Context context) {
		this.context = context;
	}

	public void initTextPaint(){
		initHightlightRowPaint();
		initNormalRowPaint();
		initTrySelectRowPaint();
		initMessagePaint();
		initSelectLinePaint();
		initTimeText();
		NormalRowTextSize = MeasureUtil.sp2px(context, 18);
		HightlightRowTextSize = MeasureUtil.sp2px(context, 18);
		TrySelectRowTextSize = MeasureUtil.sp2px(context, 18);
		MessagePaintTextSize = MeasureUtil.sp2px(context, 18);
		TimeTextSize = MeasureUtil.sp2px(context, 12);
		LinePaddings = MeasureUtil.dip2px(context, 24);
	}

	private void initTimeText() {
		if (TimeTextPaint == null) {
			TimeTextPaint = new Paint();
			TimeTextPaint.setAntiAlias(true);
			TimeTextPaint.setTextAlign(Align.LEFT);
		}
		TimeTextPaint.setTextSize(MeasureUtil.sp2px(context, 10));
		TimeTextPaint.setColor(context.getResources().getColor(R.color.music_lrc_color));
	}

	public void initSelectLinePaint() {
		if (SelectLinePaint == null) {
			SelectLinePaint = new Paint();
			SelectLinePaint.setAntiAlias(true);
			SelectLinePaint.setTextAlign(Align.LEFT);
		}
		SelectLinePaint.setTextSize(MeasureUtil.sp2px(context, 1));
		SelectLinePaint.setColor(context.getResources().getColor(R.color.music_lrc_color));
	}

	public void initMessagePaint() {
		if (MessagePaint == null) {
			MessagePaint = new Paint();
			MessagePaint.setAntiAlias(true);
			MessagePaint.setTextAlign(Align.CENTER);
		}
		MessagePaint.setTextSize(MeasureUtil.sp2px(context, 18));
		MessagePaint.setColor(context.getResources().getColor(R.color.music_lrc_color));
	}

	public void initNormalRowPaint() {
		if (NormalRowPaint == null) {
			NormalRowPaint = new Paint();
			NormalRowPaint.setAntiAlias(true);
			NormalRowPaint.setTextAlign(Align.CENTER);
		}
		NormalRowPaint.setTextSize(MeasureUtil.sp2px(context, 18));
		NormalRowPaint.setColor(Color.parseColor("#4cFFFFFF"));

	}

	public void initHightlightRowPaint() {
		if (HightlightRowPaint == null) {
			HightlightRowPaint = new Paint();
			HightlightRowPaint.setAntiAlias(true);
			HightlightRowPaint.setTextAlign(Align.CENTER);
		}
		HightlightRowPaint.setTextSize(MeasureUtil.sp2px(context, 18));
		HightlightRowPaint.setColor(Color.WHITE);
	}

	public void initTrySelectRowPaint() {
		if (TrySelectRowPaint == null) {
			TrySelectRowPaint = new Paint();
			TrySelectRowPaint.setAntiAlias(true);
			TrySelectRowPaint.setTextAlign(Align.CENTER);
		}
		TrySelectRowPaint.setTextSize(MeasureUtil.sp2px(context, 18));
		TrySelectRowPaint.setColor(context.getResources().getColor(R.color.music_lrc_color));
	}

	public enum Mode {
		normal, Seeking
	}
}
