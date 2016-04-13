package lovejazzie.convertToCoordinate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Scroller;

/**
 * Created by Administrator on 2016/2/29.
 */
public class myListView extends View {
    private Scroller scroller = new Scroller(getContext());


    public myListView(Context context) {
        super(context);
    }

    public myListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public myListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();

        }
    }
}
