package code.loginandregistration.com.helper;

import org.json.JSONObject;

/**
 * Created by Moin Khan on 2/27/2017.
 */
public interface VolleyResponseListener {
    void onError(String message);

    void onResponse(JSONObject response);
}
