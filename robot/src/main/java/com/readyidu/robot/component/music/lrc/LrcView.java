package com.readyidu.robot.component.music.lrc;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.readyidu.robot.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class LrcView extends View implements ILrcView {
    public String tag = "LrcView";
    private String message = "暂无歌词";
    private LrcViewContext lvContext;
    private List<LrcRow> mRows;

    public LrcView(Context context) {
        super(context);
        lvContext = new LrcViewContext(context);
    }

    public LrcView(Context context, AttributeSet attr) {
        super(context, attr);
        lvContext = new LrcViewContext(context);
    }

    private long ActionDownTimeMoment = 0;
    private float ActionfirstY = 0;
    private int HightLightRowPosition = 0;
    private int TrySeletRowPosition = 0;
    private float FirstRowPositionY = 0;
    private float DrawRowPositionY = 0;

    private Boolean onAnimation = false;
    private int AutomaMoveAnimationDuration = 400;
    private Boolean InitLrcRowDada = false;

    OnClickListener mClickListener;
    int Trianglewidth = 0;
    LrcViewSeekListener mSeekListsner;

    protected float mDownX;
    protected float mDownY;
    public static final int THRESHOLD = 80;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (mRows == null || mRows.size() == 0) {
            if (DoClick()) {
                return true;
            }
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                ActionfirstY = event.getY();
                ActionDownTimeMoment = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mDownX;
                float deltaY = y - mDownY;
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                if (absDeltaX > THRESHOLD || absDeltaY > THRESHOLD) {
                    if (absDeltaX < THRESHOLD) {
                        doSeek(event);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (lvContext.CurrentMode == LrcViewContext.Mode.Seeking) {
                    seekToPosition();
                }
                lvContext.CurrentMode = LrcViewContext.Mode.normal;
                Boolean NeedPerformClick = System.currentTimeMillis() - ActionDownTimeMoment < 200;
                if (NeedPerformClick) {
                    DoClick();
                }
                break;
        }
        return true;
    }

    private void seekToPosition() {
//        HightLightRowPosition = TrySeletRowPosition;
        invalidate();
        if (mSeekListsner != null) {
            mSeekListsner.onSeek(mRows.get(HightLightRowPosition), mRows.get(HightLightRowPosition).CurrentRowTime);
        }
    }

    private void doSeek(MotionEvent event) {
        lvContext.CurrentMode = LrcViewContext.Mode.Seeking;
        float currenty = event.getY();
        float offesty = currenty - ActionfirstY;// 移动偏移量
        FirstRowPositionY = FirstRowPositionY + offesty;
        int rowOffset = Math.abs((int) (offesty / lvContext.NormalRowTextSize));
        if (offesty < 0) {
            TrySeletRowPosition += rowOffset;
        } else if (offesty > 0) {
            TrySeletRowPosition -= rowOffset;
        }
        TrySeletRowPosition = Math.max(0, TrySeletRowPosition);
        TrySeletRowPosition = Math.min(TrySeletRowPosition, mRows.size() - 1);
        MakeFirstRowPositionSecure();
        invalidate();
        ActionfirstY = currenty;
    }

    private void MakeFirstRowPositionSecure() {
        float defaultfirstrowy = getHeight() / 2;
        FirstRowPositionY = Math.min(FirstRowPositionY, defaultfirstrowy);
        if (FirstRowPositionY == 0) {
            FirstRowPositionY = defaultfirstrowy;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!InitLrcRowDada) {
            InitLrcRowDada = true;
            InitLrcRowData(mRows);
        }
        int viewWidth = getViewWidth();
        int viewHeight = getViewHeight();
        // 如果没有数据，画出信息文件
        if (ListIsEmpty(mRows)) {
            if (!TextUtils.isEmpty(message)) {
                canvas.drawText(message + "", viewWidth / 2, viewHeight / 2 - lvContext.MessagePaintTextSize / 2,
                        lvContext.MessagePaint);
            }
            return;
        }
        float rowx = viewWidth / 2;
        float timeliney = viewHeight / 2;
        int linepadding = lvContext.HightlightRowTextSize + lvContext.LinePaddings;
        MakeFirstRowPositionSecure();
        DrawRowPositionY = FirstRowPositionY;
        // 画出歌词
        for (int i = 0; i < mRows.size(); i++) {
            LrcRow lrcRow = mRows.get(i);
            if (lvContext.CurrentMode == LrcViewContext.Mode.normal) {
                if (i == HightLightRowPosition) {
                    DrawHightLightRow(i, lrcRow, canvas, rowx);
                } else {
                    DrawNormalRow(i, lrcRow, canvas, rowx);
                }
            } else {
                float timelineytop = timeliney - (linepadding / 2) * lrcRow.getShowRows().size();
                float timelineybottom = timeliney + (linepadding / 2) * lrcRow.getShowRows().size();
                if (i == HightLightRowPosition) {
//					DrawHightLightRow(i, lrcRow, canvas, rowx);
                    DrawNormalRow(i, lrcRow, canvas, rowx);
                } else if ((DrawRowPositionY + linepadding) > timelineytop
                        && (DrawRowPositionY + linepadding) < timelineybottom) {
                    DrawTrySelectRow(i, lrcRow, canvas, rowx);
                } else {
                    DrawNormalRow(i, lrcRow, canvas, rowx);
                }
//				if (i == HightLightRowPosition) {
//					DrawHightLightRow(i, lrcRow, canvas, rowx);
//				} else {
//					DrawNormalRow(i, lrcRow, canvas, rowx);
//				}
            }
        }
    }

    private void DrawNormalRow(int rawindex, LrcRow lrcRow, Canvas canvas, float rowx) {
        int linepadding = lvContext.NormalRowTextSize + lvContext.LinePaddings;
        List<LrcShowRow> showrows = lrcRow.getShowRows();
        for (LrcShowRow sr : showrows) {
            DrawRowPositionY += linepadding;
            sr.YPosition = DrawRowPositionY;
            canvas.drawText(sr.Data + "", rowx, DrawRowPositionY, lvContext.NormalRowPaint);
        }
    }

    private void DrawTrySelectRow(int rawindex, LrcRow lrcRow, Canvas canvas, float rowx) {
        int linepadding = lvContext.TrySelectRowTextSize + lvContext.LinePaddings;
        List<LrcShowRow> showrows = lrcRow.getShowRows();
        for (LrcShowRow sr : showrows) {
            DrawRowPositionY += linepadding;
            sr.YPosition = DrawRowPositionY;
            canvas.drawText(sr.Data + "", rowx, DrawRowPositionY, lvContext.TrySelectRowPaint);
        }

        TrySeletRowPosition = rawindex;
    }

    private void DrawHightLightRow(int rawindex, LrcRow lrcRow, Canvas canvas, float rowx) {
        int linepadding = lvContext.HightlightRowTextSize + lvContext.LinePaddings;
        List<LrcShowRow> showrows = lrcRow.getShowRows();
        for (LrcShowRow sr : showrows) {
            DrawRowPositionY += linepadding;
            sr.YPosition = DrawRowPositionY;
            canvas.drawText(sr.Data + "", rowx, DrawRowPositionY, lvContext.HightlightRowPaint);
        }
    }

    private float MeasureText(String text, Paint paint) {
        if (TextUtils.isEmpty(text))
            return 0;
        return paint.measureText(text);
    }

    private Boolean DoClick() {
        if (mClickListener != null) {
            mClickListener.onClick(null);
            return true;
        }
        return false;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mClickListener = l;
    }

    @Override
    public void setLrcData(List<LrcRow> lrcRows) {
        InitLrcRowDada = false;
        mRows = lrcRows;
        postInvalidate();
    }

    public void setEmptyMessage() {
        setLrcViewMessage("暂无歌词");
        postInvalidate();
    }

    private void InitLrcRowData(List<LrcRow> lrcRows) {
        initLrcView();

        if (lrcRows == null || lrcRows.size() == 0)
            return;
        float linewidth = getWidth() * 6 / 7;
        int index = 0;
        for (LrcRow r : lrcRows) {
            String content = r.getRowData();
            if (!TextUtils.isEmpty(content)) {
                List<String> lines = MakeSecureLines(content, lvContext.NormalRowPaint, linewidth);
                for (String line : lines) {
                    LrcShowRow showRow = new LrcShowRow(index++, line, lvContext.NormalRowTextSize,
                            lvContext.LinePaddings);
                    r.getShowRows().add(showRow);
                }
            }
        }
    }

    private void initLrcView() {
        int textheight = getViewHeight() / 20 - lvContext.LinePaddings;
        Trianglewidth = getViewWidth() / 50;
        lvContext.NormalRowTextSize = textheight;
        lvContext.HightlightRowTextSize = textheight;
        lvContext.TrySelectRowTextSize = textheight;
        lvContext.TimeTextSize = textheight * 2 / 3;
        lvContext.LinePaddings = textheight;
        //lvContext.NormalRowColor =
        lvContext.initTextPaint();
    }

    private List<String> MakeSecureLines(String text, Paint mPaint, float securewidth) {
        List<String> lines = new ArrayList<String>();
        if (TextUtils.isEmpty(text)) {
            return lines;
        }
        float maxwidth = securewidth;
        int measurednum = mPaint.breakText(text, true, maxwidth, null);
        lines.add(text.substring(0, measurednum));
        String leftstr = text.substring(measurednum);
        while (leftstr.length() > 0) {
            measurednum = mPaint.breakText(leftstr, true, maxwidth, null);
            if (measurednum > 0) {
                lines.add(leftstr.substring(0, measurednum));
            }
            leftstr = leftstr.substring(measurednum);
        }

        return lines;
    }

    @Override
    public void setLrcViewSeekListener(LrcViewSeekListener seekListener) {
        this.mSeekListsner = seekListener;
    }

    @Override
    public void setLrcViewMessage(String messagetext) {
        this.message = messagetext;
    }

    private void StartMoveAnimation(LrcRow tryselectrow, final int tryselectrowindex) {

        if (tryselectrowindex == HightLightRowPosition) {
            return;
        }
        if (tryselectrow == null) {
            return;
        }

        List<LrcShowRow> hightlightrs = mRows.get(HightLightRowPosition).getShowRows();
        List<LrcShowRow> tryrs = tryselectrow.getShowRows();
        if (ListIsEmpty(hightlightrs) || ListIsEmpty(tryrs))
            return;
        float hightlightrowshowY = getTimeLineYPosition() + lvContext.HightlightRowTextSize / 2;
        float tryselectrowY = tryrs.get(0).YPosition;
        final float distance = hightlightrowshowY - tryselectrowY;
        final float FirstRowPositionPreY = FirstRowPositionY;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, distance);
        valueAnimator.setDuration(AutomaMoveAnimationDuration);
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                FirstRowPositionY = FirstRowPositionPreY + value;
                invalidate();
            }
        });

        valueAnimator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                HightLightRowPosition = tryselectrowindex;
                invalidate();
                onAnimation = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        onAnimation = true;
        valueAnimator.start();
    }

    public int getViewWidth() {
        return getWidth();
    }

    public int getViewHeight() {
        return getHeight();
    }

    private Boolean ListIsEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    private int getTimeLineYPosition() {
        return getViewHeight() / 2;
    }

    @Override
    public void setCurrentTime(long time) {
        seekLrcToTime(time);
    }

    public void seekLrcToTime(long time) {
        LogUtils.i("Music", "seekLrcToTime:" + time);
        if (ListIsEmpty(mRows)) {
            return;
        }
        if (onAnimation) {
            return;
        }
        for (int i = 0; i < mRows.size(); i++) {
            LrcRow current = mRows.get(i);
            LrcRow next = i + 1 == mRows.size() ? null : mRows.get(i + 1);
            if ((time >= current.CurrentRowTime
                    && next != null
                    && time < next.CurrentRowTime)
                    || (time > current.CurrentRowTime
                    && next == null)) {
                StartMoveAnimation(current, i);
                return;
            }
        }
    }

}
