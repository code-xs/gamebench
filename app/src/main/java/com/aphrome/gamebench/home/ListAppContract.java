package com.aphrome.gamebench.home;

import java.util.List;

import com.aphrome.gamebench.abs.BasePresenter;
import com.aphrome.gamebench.abs.BaseView;
import com.aphrome.gamebench.home.models.AppInfo;

/**
 * @author Lody
 * @version 1.0
 */
/*package*/ class ListAppContract {
    interface ListAppView extends BaseView<ListAppPresenter> {

        void startLoading();

        void loadFinish(List<AppInfo> infoList);
    }

    interface ListAppPresenter extends BasePresenter {

    }
}
