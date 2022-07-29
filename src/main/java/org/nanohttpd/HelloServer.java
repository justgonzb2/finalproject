package org.nanohttpd;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Base application class for setting up NanoHTTPD server.
 *
 * @author justin
 */
public class HelloServer extends NanoHTTPD {

    private static final String DB_CONNECTION = "jdbc:mysql://127.0.0.1:3306/mydatabase";

    mysqldatabase connection = new mysqldatabase(DB_CONNECTION);

    public HelloServer() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public static void main(String[] args) {
        try {
            new HelloServer();
        } catch (IOException ioe) {
            System.err.println(ioe.getCause());
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (session.getMethod() == Method.GET) {
            String jsonResp = null;
            String path = session.getUri();
            String param = cleanValue(session.getUri());
            Gson gson = new Gson();

            if (path != null) {
                if (path.contains("temp")) {
                    if (param != null && !param.equals("")) {
                        temp temp = connection.getTemp(param);
                        if (temp == null) {
                            return newFixedLengthResponse("empty temp value");
                        }
                        jsonResp = gson.toJson(temp);
                    } else {
                        List<temp> temps = connection.getAllTemps();
                        jsonResp = gson.toJson(temps);
                    }
                } else if (path.contains("state")) {
                    status state = connection.getState();
                    jsonResp = Boolean.toString(state.isOn());
                }

                return newFixedLengthResponse(jsonResp);
            }
            return newFixedLengthResponse("improper get url path");
        } else if (session.getMethod() == Method.POST) {
            try {
                session.parseBody(new HashMap<>());
                String path = session.getUri().replace("/", "");
                String result = null;
                String input = session.getQueryParameterString();
                if (path.equals("temp")) {
                    String[] values = input.split(",");
                    String id = values[0];
                    int temp = Integer.parseInt(values[1]);
                    int temp2 = Integer.parseInt(values[2]);
                    result = connection.updateTemp((temp) new temp(temp, temp2, id));
                } else if (path.equals("update")) {
                    int temp = Integer.parseInt(cleanDecimal(input));
                    handleTemperatureChange(temp);
                }

                return newFixedLengthResponse(result);
            } catch (IOException | NanoHTTPD.ResponseException e) {
                return newFixedLengthResponse("unable to commit post");
            }
        } else if (session.getMethod() == Method.PUT) {
            // same as post
        } else if (session.getMethod() == Method.DELETE) {
            String path = session.getUri().replace("/", "");
            if (path.equals("temp")) {
                String result = connection.deleteTemp(cleanValue(session.getUri()));
                return newFixedLengthResponse(result);
            } else if (path.equals("temp")) {
                String result = connection.deleteTemp(cleanValue(session.getUri()));
                return newFixedLengthResponse(result);
            }

            return newFixedLengthResponse("delete failed");
        }

        return newFixedLengthResponse("wrong path, try again");
    }

    private String cleanDecimal(String input) {
        return cleanValue(input.replace(".000000", ""));
    }

    private String cleanValue(String param) {
        return param.replaceAll("[^0-9]", "");
    }

    private String decodePeriod() {
        Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        if (hour >= 18) {
            return "NIGHT";
        } else if (hour >= 12) {
            return "PM";
        } else {
            return "AM";
        }
    }

    private String handleTemperatureChange(int rTemp) {
        String timeofday = decodePeriod();
        temp setting = connection.getTemperatureSetting(timeofday);
        if (rTemp < setting.getTemp2() && rTemp > setting.getTemp()) {
            // noop
        } else if (rTemp > setting.getTemp2()) {
            return connection.updateState(false);
        } else if (rTemp < setting.getTemp()) {
            return connection.updateState(true);
        }

        return null;
    }
}
