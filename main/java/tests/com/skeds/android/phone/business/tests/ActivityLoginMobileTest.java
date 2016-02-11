/**
 * Created by Mikhail on 29.07.2014.
 */
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;

import com.robotium.solo.Solo;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.activities.ActivityCustomerLocationListView;
import com.skeds.android.phone.business.activities.ActivityDashboardView;
import com.skeds.android.phone.business.activities.ActivityLoginMobile;

@SuppressWarnings("rawtypes")
public class ActivityLoginMobileTest extends ActivityInstrumentationTestCase2 {


    @SuppressWarnings("unchecked")
    public ActivityLoginMobileTest() throws ClassNotFoundException {
        super(ActivityLoginMobile.class);
    }

    private Solo solo;

    @Override
    protected void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testCanOpenSettings(){
        solo.assertCurrentActivity("Error",ActivityLoginMobile.class);
        EditText user_name = (EditText) solo.getView(R.id.user_name);
        EditText password = (EditText) solo.getView(R.id.password);
        solo.clearEditText(user_name);
        solo.clearEditText(password);
        solo.enterText(user_name,"dtoretto");
        solo.enterText(password,"dtoretto");
        View view = solo.getView(R.id.login);
        view.callOnClick();

        solo.waitForActivity(ActivityDashboardView.class);
        solo.assertCurrentActivity("Not logined as toretto", ActivityDashboardView.class);
    }


    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();

    }


}