package com.bjz.jzappframe;

import com.bjz.jzappframe.bean.JZLoadingBean;

public interface IJZBaseView {

    void showLoad(JZLoadingBean loadingBean);

    void hideLoad();

}
