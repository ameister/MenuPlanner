package ch.bbv.menuplanner.persistence;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import ch.bbv.menuplanner.bo.Condiment;
import ch.bbv.menuplanner.bo.CondimentPos;
import ch.bbv.menuplanner.bo.Day;
import ch.bbv.menuplanner.bo.Menu;

public class DayRepository extends AsyncTask<Day, Void, List<Day>> implements Serializable{
	private static final long serialVersionUID = 1L;
	@SuppressLint("SimpleDateFormat")
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private final JSONParser parser = new JSONParser();
	private final Set<OnTaskCompletedListener<List<Day>>> dayListeners = new HashSet<OnTaskCompletedListener<List<Day>>>();
	private final String url;
	private final boolean offline;
	
	public DayRepository(String serverIp) {
		offline = serverIp == null;
		StringBuilder sb = new StringBuilder("http://");
		sb.append(serverIp);
		sb.append("/xampp/menuPlannerAndroid/");
		this.url = sb.toString();
	}

	//TODO caching
	//TODO Refactor
	private List<Day> populateMenus(Day start, Day end, JSONArray jsonArray) throws JSONException, ParseException {
		List<Day> days = new ArrayList<Day>();
		List<Menu> menusPerDay = new ArrayList<Menu>();
		Day day = null;
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			long id = jsonObject.getLong("dayId");
			Date date = df.parse(jsonObject.getString("dayDate"));
			if(day == null || day.getId() != id) {
				if(day != null) {
					day.addAllMenus(menusPerDay);
					days.add(day);
				}
				menusPerDay.clear();
				day = new Day(id, date);
			}
			Menu menu = new Menu();
			menu.setName(jsonObject.getString("name"));
			menu.setId(jsonObject.getLong("menuId"));
			ArrayList<NameValuePair> menuParams = new ArrayList<NameValuePair>();
			menuParams.add(new BasicNameValuePair("menuId", Long.toString(menu.getId())));
			JSONArray condimentsArray = parser.makeHttpRequest(url + "findCondiments.php", "POST", menuParams);
			for (int j = 0; j < condimentsArray.length(); j++) {
				JSONObject conObject = condimentsArray.getJSONObject(j);
				CondimentPos pos = new CondimentPos();
				pos.setId(conObject.getLong("cp.id"));
				pos.setAmount(new BigDecimal(conObject.getString("amount")));
				pos.setUnit(conObject.getString("unit"));
				Condiment con = new Condiment(conObject.getString("name"));
				con.setId(conObject.getLong("c.id"));
				pos.setCondiment(con);
				pos.setMenu(menu);
				menu.addPos(pos);
				menu.setDay(day);
			}
			menusPerDay.add(menu);
		}
		if(day != null) {
			day.addAllMenus(menusPerDay);
			days.add(day);
		} else {
			days.add(start);
		}
		menusPerDay.clear();
		return days;
	}
	
	public List<Day> findAllFor(Day start, Day end) {
		final List<Day> result = new ArrayList<Day>();
		if(offline) {
			result.add(start);
		} else {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("start", df.format(start.getDate())));
			params.add(new BasicNameValuePair("end", df.format(end.getDate())));
			JSONArray jsonArray = parser.makeHttpRequest(url + "findMenusForRange.php", "POST", params);
			
			try {
				result.addAll(populateMenus(start, end, jsonArray));
			} catch (JSONException e) {
				Log.e("PopulateMenus Error", "Error populating Menus: " + jsonArray.toString(), e);
			} catch (ParseException e) {
				Log.e("PopulateMenus Error", "Unable to parse: " + jsonArray.toString(), e);
			}
			
		}
		return result;
	}
	
	public void addDayListener(OnTaskCompletedListener<List<Day>> listener){
		dayListeners.add(listener);
	}
	
	public void removeDayListener (OnTaskCompletedListener<List<Day>> listener){
		dayListeners.remove(listener);
	}

	@Override
	protected List<Day> doInBackground(Day...days) {
		final List<Day> resultDays;
		if(days.length > 1) {
			resultDays = findAllFor(days[0], days[1]);
		} else {
			resultDays = findAllFor(days[0], days[0]);
		}
		return resultDays;
	}
	
	@Override
	protected void onPostExecute(List<Day> result) {
		super.onPostExecute(result);
		for (OnTaskCompletedListener<List<Day>> listener : dayListeners) {
			listener.onTaskCompleted(result);
		}
		dayListeners.clear();
	}
}
