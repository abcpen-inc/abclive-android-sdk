package com.abc.live.widget.wb;

/**
 * Created by dear33 on 2016/9/11.
 */
public interface ButtonEventListener {
    /**
     * @param index button index, count from startAngle to endAngle, value is 1 to expandButtonCount
     */
    void onButtonClicked(ButtonData view, int index);

    void onExpand(AllAngleExpandableButton view);

    void onCollapse(AllAngleExpandableButton view);

    void onParentClicked(AllAngleExpandableButton view);

}
