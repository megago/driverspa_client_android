package com.driverspa.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.model.BookInfo;
import com.driverspa.model.CarItem;
import com.driverspa.model.CarType;
import com.driverspa.model.PhotoOrm;
import com.driverspa.model.Statistic;
import com.driverspa.model.User;
import com.driverspa.model.Washer;
import com.driverspa.util.Constants;
import com.driverspa.util.Functions;
import com.driverspa.util.L;
import com.driverspa.util.UserPreferences;

/**
 * @author Yerzhan
 *
 */

public class WashmeOrmLiteSqlHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "com.driverspa.db";
	private static final int DATABASE_VERSION = 4;
	
	private Dao<User, String> userDao = null;
	private RuntimeExceptionDao<User, String> userRuntimeDao;

	private Dao<PhotoOrm, String> userPhotoDao = null;
	private RuntimeExceptionDao<PhotoOrm, String> userPhotoRuntimeDao;

	private Dao<CarItem, String> userCarsDao = null;
	private RuntimeExceptionDao<CarItem, String> userCarsRuntimeDao;

	private Dao<BookInfo, String> bookingDao = null;
	private RuntimeExceptionDao<BookInfo, String> bookingRuntimeDao;

	public WashmeOrmLiteSqlHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, User.class);
			TableUtils.createTable(connectionSource, PhotoOrm.class);
			TableUtils.createTable(connectionSource, CarItem.class);
			TableUtils.createTable(connectionSource, BookInfo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion,
			int newVersion) {
		try {
			BA.getContext().deleteDatabase(DATABASE_NAME);
	        onCreate(database);
//			UserPreferences.onUserLogout(BA.getContext());
//			UserPreferences.onAdminLogout(BA.getContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Dao<User, String> getUserDao() throws SQLException {
		if( userDao == null)
			userDao = getDao(User.class);
		return userDao;
	}

	public RuntimeExceptionDao<User, String> getUserRuntimeExceptionDao() {
		if( userRuntimeDao == null)
			userRuntimeDao = getRuntimeExceptionDao(User.class);
		return userRuntimeDao;
	}
	
	public Dao<PhotoOrm, String> getUserPhotoDao() throws SQLException {
		if( userPhotoDao == null)
			userPhotoDao = getDao(PhotoOrm.class);
		return userPhotoDao;
	}

	public RuntimeExceptionDao<PhotoOrm, String> getUserPhotoRuntimeExceptionDao() {
		if( userPhotoRuntimeDao == null)
			userPhotoRuntimeDao = getRuntimeExceptionDao(PhotoOrm.class);
		return userPhotoRuntimeDao;
	}

	public Dao<CarItem, String> getUserCarsDao() throws SQLException {
		if( userCarsDao == null)
			userCarsDao = getDao(CarItem.class);
		return userCarsDao;
	}

	public RuntimeExceptionDao<CarItem, String> getUserCarsRuntimeExceptionDao() {
		if( userCarsRuntimeDao == null)
			userCarsRuntimeDao = getRuntimeExceptionDao(CarItem.class);
		return userCarsRuntimeDao;
	}

	public Dao<BookInfo, String> getBookingDao() throws SQLException {
		if( bookingDao == null)
			bookingDao = getDao(BookInfo.class);
		return bookingDao;
	}

	public RuntimeExceptionDao<BookInfo, String> getBookingRuntimeDao() {
		if( bookingRuntimeDao == null)
			bookingRuntimeDao = getRuntimeExceptionDao(BookInfo.class);
		return bookingRuntimeDao;
	}

	public void removeAll()
	{
		// db.delete(String tableName, String whereClause, String[] whereArgs);
		// If whereClause is null, it will delete all rows.
		SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
		db.delete("User", null, null);
		db.delete("PhotoOrm", null, null);
		db.delete("CarItem", null, null);
		db.delete("BookInfo", null, null);
	}

	public void removePhotos()
	{
		// db.delete(String tableName, String whereClause, String[] whereArgs);
		// If whereClause is null, it will delete all rows.
		SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
		db.delete("PhotoOrm", null, null);
	}


	public void deleteAllBooksData()
	{
		// db.delete(String tableName, String whereClause, String[] whereArgs);
		// If whereClause is null, it will delete all rows.
		SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
		db.delete("BookInfo", null, null);
	}

	public List<Statistic> getStatisticData(Washer washer, Date date){
			List<Statistic> statistics = new ArrayList<Statistic>();

		try {
			// Total counts and for cartypes
			if (washer != null && washer.getCarTypes() != null && washer.getCarTypes().size() > 0) {

				final HashMap<Integer, String> allCarTypes = BA.getReference().getCarType();
				Statistic statistic = new Statistic();
				List<Statistic.StatisticDetail> statisticDetails = new ArrayList<Statistic.StatisticDetail>();
				statistic.setTitle("Общие показатели");

				//Total count of booked and queued items
				Cursor c = null;
				c = this.getWritableDatabase().rawQuery("SELECT count(*) as cnt FROM bookinfo where status in ('finished','queued_finished')", null);

				if (c != null && c.getCount() != 0) {
					for (int i = 0; i < c.getCount(); i++) {
						c.moveToPosition(i);
						Statistic.StatisticDetail statisticDetail = new Statistic.StatisticDetail("Количество машин", c.getString(c.getColumnIndex("cnt")));
						statisticDetails.add(statisticDetail);
						L.d("getting "+c.getString(c.getColumnIndex("cnt")));
					}
				}

				if (washer != null && washer.getCarTypes() != null && washer.getCarTypes().size() > 0) {
					//Booked items for each carType
					for (Integer carType : washer.getCarTypes()) {
						c = null;
						c = this.getWritableDatabase().rawQuery("SELECT count(*) as cnt FROM bookinfo where status in ('finished','queued_finished') and cartype=" + carType, null);

						if (c != null && c.getCount() != 0) {
							for (int i = 0; i < c.getCount(); i++) {
								c.moveToPosition(i);
								Statistic.StatisticDetail statisticDetail = new Statistic.StatisticDetail(allCarTypes.get(carType), c.getString(c.getColumnIndex("cnt")));
								statisticDetails.add(statisticDetail);
							}
						}
					}
				}

				//Total sum of books
				c = null;
				c = this.getWritableDatabase().rawQuery("SELECT sum(CAST(price AS INTEGER)) as sum FROM bookinfo where status in ('finished','queued_finished')", null);

				if (c != null && c.getCount() != 0) {
					for (int i = 0; i < c.getCount(); i++) {
						c.moveToPosition(i);
						Statistic.StatisticDetail statisticDetail = new Statistic.StatisticDetail("Общая сумма", (TextUtils.isEmpty(c.getString(c.getColumnIndex("sum"))) ? "0" : c.getString(c.getColumnIndex("sum"))) + " ₸");
						statisticDetails.add(statisticDetail);
					}
				}
				statistic.setStatisticsList(statisticDetails);
				statistics.add(statistic);

				statistic = new Statistic();
				statisticDetails = new ArrayList<Statistic.StatisticDetail>();
				statistic.setTitle("Живая очередь");
				//Total count of booked items
				c = null;
				c = this.getWritableDatabase().rawQuery("SELECT count(*) as cnt FROM bookinfo where status in ('finished','queued_finished') and queued=1", null);

				if (c != null && c.getCount() != 0) {
					for (int i = 0; i < c.getCount(); i++) {
						c.moveToPosition(i);
						Statistic.StatisticDetail statisticDetail = new Statistic.StatisticDetail("Количество машин", c.getString(c.getColumnIndex("cnt")));
						statisticDetails.add(statisticDetail);
					}
				}

				//Booked items for each carType
				if (washer != null && washer.getCarTypes() != null && washer.getCarTypes().size() > 0) {
					for (Integer carType : washer.getCarTypes()) {
						c = null;
						c = this.getWritableDatabase().rawQuery("SELECT count(*) as cnt FROM bookinfo where status in ('finished','queued_finished') and queued=1 and cartype=" + carType, null);

						if (c != null && c.getCount() != 0) {
							for (int i = 0; i < c.getCount(); i++) {
								c.moveToPosition(i);
								Statistic.StatisticDetail statisticDetail = new Statistic.StatisticDetail(allCarTypes.get(carType), c.getString(c.getColumnIndex("cnt")));
								statisticDetails.add(statisticDetail);
							}
						}
					}
				}

				//Total sum of books
				c = null;
				c = this.getWritableDatabase().rawQuery("SELECT sum(CAST(price AS INTEGER)) as sum FROM bookinfo where status in ('finished','queued_finished') and queued=1", null);

				if (c != null && c.getCount() != 0) {
					for (int i = 0; i < c.getCount(); i++) {
						c.moveToPosition(i);
						Statistic.StatisticDetail statisticDetail = new Statistic.StatisticDetail("Общая сумма", (TextUtils.isEmpty(c.getString(c.getColumnIndex("sum"))) ? "0" : c.getString(c.getColumnIndex("sum"))) + " ₸");
						statisticDetails.add(statisticDetail);
					}
				}
				statistic.setStatisticsList(statisticDetails);
				statistics.add(statistic);


				statistic = new Statistic();
				statisticDetails = new ArrayList<Statistic.StatisticDetail>();
				statistic.setTitle("Брони");
				//Total count of booked items
				c = null;
				c = this.getWritableDatabase().rawQuery("SELECT count(*) as cnt FROM bookinfo where status in ('finished','queued_finished') and queued=0", null);

				if (c != null && c.getCount() != 0) {
					for (int i = 0; i < c.getCount(); i++) {
						c.moveToPosition(i);
						Statistic.StatisticDetail statisticDetail = new Statistic.StatisticDetail("Количество машин", c.getString(c.getColumnIndex("cnt")));
						statisticDetails.add(statisticDetail);
					}
				}

				//Booked items for each carType
				if (washer != null && washer.getCarTypes() != null && washer.getCarTypes().size() > 0) {
					for (Integer carType : washer.getCarTypes()) {
						c = null;
						c = this.getWritableDatabase().rawQuery("SELECT count(*) as cnt FROM bookinfo where status in ('finished','queued_finished') and queued=0 and cartype=" + carType, null);

						if (c != null && c.getCount() != 0) {
							for (int i = 0; i < c.getCount(); i++) {
								c.moveToPosition(i);
								Statistic.StatisticDetail statisticDetail = new Statistic.StatisticDetail(allCarTypes.get(carType), c.getString(c.getColumnIndex("cnt")));
								statisticDetails.add(statisticDetail);
							}
						}
					}
				}

				//Total sum of books
				c = null;
				c = this.getWritableDatabase().rawQuery("SELECT sum(CAST(price AS INTEGER)) as sum FROM bookinfo where status in ('finished','queued_finished') and queued=0", null);

				if (c != null && c.getCount() != 0) {
					for (int i = 0; i < c.getCount(); i++) {
						c.moveToPosition(i);
						Statistic.StatisticDetail statisticDetail = new Statistic.StatisticDetail("Общая сумма", (TextUtils.isEmpty(c.getString(c.getColumnIndex("sum"))) ? "0" : c.getString(c.getColumnIndex("sum"))) + " ₸");
						statisticDetails.add(statisticDetail);
					}
				}
				statistic.setStatisticsList(statisticDetails);
				statistics.add(statistic);


				statistic = new Statistic();
				statisticDetails = new ArrayList<Statistic.StatisticDetail>();
				statistic.setTitle("По боксам");
				//Total count of booked items
				c = null;
				c = this.getWritableDatabase().rawQuery("SELECT count(*) as cnt FROM bookinfo where status in ('finished','queued_finished')", null);

				if (washer != null && washer.getBoxSettings() != null && washer.getBoxSettings().size() > 0) {
					for (Washer.BoxSettings bs : washer.getBoxSettings()) {
						c = null;
						c = this.getWritableDatabase().rawQuery("SELECT count(*) as cnt,sum(CAST(price AS INTEGER)) as sum FROM bookinfo where status in ('finished','queued_finished') and boxId='" + bs.getUid() + "'", null);

						if (c != null && c.getCount() != 0) {
							for (int i = 0; i < c.getCount(); i++) {
								c.moveToPosition(i);
								Statistic.StatisticDetail statisticDetail = new Statistic.StatisticDetail(Functions.getBoxName(washer, bs.getUid())
																										  +"("+bs.getBookingType()+")"+(TextUtils.isEmpty(bs.getWasherPerson())?"":" - "+bs.getWasherPerson())
																											, c.getString(c.getColumnIndex("cnt"))
										+ " на сумму " + (TextUtils.isEmpty(c.getString(c.getColumnIndex("sum"))) ? "0" : c.getString(c.getColumnIndex("sum"))) + " ₸"
								);
								statisticDetails.add(statisticDetail);
							}
						}
					}
				}

				statistic.setStatisticsList(statisticDetails);
				statistics.add(statistic);

				c.close();
			}

		}
		catch(Exception e){
			e.printStackTrace();
		}
		return statistics;
	}



}
