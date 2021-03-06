package code.loginandregistration.com.helper;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Moin Khan on 2/27/2017.
 */
public class VolleyWebService {
    static String tag_json_obj = "json_obj_req";

    public static void makeJsonObjectRequest(Context context, int method, String url, JSONObject jsonObject , final VolleyResponseListener listener) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, null, "Loading...", false, false);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (method, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onResponse(response);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.toString());
                        progressDialog.dismiss();
                    }
                }) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers /**, PROTOCOL_CHARSET**/));
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }
}
