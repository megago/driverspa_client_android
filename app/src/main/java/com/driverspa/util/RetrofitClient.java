package com.driverspa.util;

import android.text.TextUtils;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.driverspa.BA;
import com.driverspa.model.Device;
import com.driverspa.model.FareRequest;
import com.driverspa.model.Settings;
import com.driverspa.model.UserLocation;
import com.driverspa.model.Washer;
import com.driverspa.model.WasherBase;
import com.driverspa.model.api.request.AddReviewRequest;
import com.driverspa.model.api.request.AuthAdminRegistrationRequest;
import com.driverspa.model.api.request.AuthClientRegistrationRequest;
import com.driverspa.model.api.request.AuthVerificationRequest;
import com.driverspa.model.api.request.CheckPhoneRequest;
import com.driverspa.model.api.request.ForgotPasswordRequest;
import com.driverspa.model.api.request.LoginPasswordRequest;
import com.driverspa.model.api.request.ResetPasswordRequest;
import com.driverspa.model.api.request.ResendActivationRequest;
import com.driverspa.model.api.request.SetPasswordRequest;
import com.driverspa.model.api.request.WhatsappRequest;
import com.driverspa.model.api.request.WhatsappStatusRequest;
import com.driverspa.model.api.request.BookingRequest;
import com.driverspa.model.api.request.BoxOperationRequest;
import com.driverspa.model.api.request.InitialWasherCreateRequest;
import com.driverspa.model.api.request.UserUpdateRequest;
import com.driverspa.model.api.request.WasherRequest;
import com.driverspa.model.api.response.AboutUSResponseHolder;
import com.driverspa.model.api.response.AdminBoxResponseHolder;
import com.driverspa.model.api.response.AdminBoxesResponseHolder;
import com.driverspa.model.api.response.AuthAdminRegistrationResponseHolder;
import com.driverspa.model.api.response.AuthAdminVerificationResponseHolder;
import com.driverspa.model.api.response.AuthClientRegistrationResponseHolder;
import com.driverspa.model.api.response.AuthClientVerificationResponseHolder;
import com.driverspa.model.api.response.CheckPhoneResponseHolder;
import com.driverspa.model.api.response.OkResponseHolder;
import com.driverspa.model.api.response.WhatsappRequestResponseHolder;
import com.driverspa.model.api.response.WhatsappStatusResponseHolder;
import com.driverspa.model.api.response.BaseResponseHolder;
import com.driverspa.model.api.response.BookInfoResponseHolder;
import com.driverspa.model.api.response.BookResponseHolder;
import com.driverspa.model.api.response.BooksResponseHolder;
import com.driverspa.model.api.response.ClientResponseHolder;
import com.driverspa.model.api.response.FareRequestResponseHolder;
import com.driverspa.model.api.response.FareRequestsResponseHolder;
import com.driverspa.model.api.response.NotificationResponseHolder;
import com.driverspa.model.api.response.PhotoResponseHolder;
import com.driverspa.model.api.response.QueuedBooksResponseHolder;
import com.driverspa.model.api.response.ReferenceResponseHolder;
import com.driverspa.model.api.response.UserResponseHolder;
import com.driverspa.model.api.response.WasherResponseHolder;
import com.driverspa.model.api.response.WasherTimeTableResponseHolder;
import com.driverspa.model.api.response.WashersResponseHolder;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;

/**
 * HTTP client for connecting to REST API
 */
public class RetrofitClient {

//    static String ip = "http://192.168.0.100";
//	public static String API_URL = ip+":8000/api/"; // api url
//	public static String API_URL_IMAGES = ip+":8000"; // api url

//	public static String API_URL = "http://192.168.43.61:8000/api/"; // api url
//	public static String API_URL_IMAGES = "http://192.168.43.61:8000"; // api url
//
//	public static String API_URL = "http://192.168.1.17:8000/api/"; // api url
//	public static String API_URL_IMAGES = "http://192.168.1.17:8000"; // api url

//	public static String API_URL = "http://192.168.1.178:8000/api/"; // api url
//	public static String API_URL_IMAGES = "http://192.168.1.178:8000"; // api url


//	public static String API_URL = "http://192.168.31.177:8000/api/"; // api url
//	public static String API_URL_IMAGES = "http:// 192.168.31.177:8000"; // api url

//	/////prod
	public static String API_URL = "https://api.driverspa.kz/api/"; // api url
	public static String API_URL_IMAGES = "https://api.driverspa.kz"; // api url

	/////test
//	public static String API_URL = "http://test.api.washme.kz/"; // api url
//	public static String API_URL_IMAGES = "http://test.api.washme.kz"; // api url

//	public static String API_URL = "http://balhash.washme.kz:5000/api/"; // api url
//	public static String API_URL_IMAGES = "http://balhash.washme.kz:5000"; // api url

	/**
	 * Interface for connecting to Washme API
	 */
	public interface WashmeApi {
		@GET("/v1/public_carwash/ref")
		void getReference(
				@Header("Accept-Language") String lang,
				Callback<ReferenceResponseHolder> callback);

		@GET("/v1/about_us/html/")
		void getAboutUS(
				Callback<AboutUSResponseHolder> callback);
		
		@POST("/v1/account/check_phone")
		void checkPhone(
				@Body CheckPhoneRequest body,
				Callback<CheckPhoneResponseHolder> callback);

		@POST("/v1/account/set_password")
		void setPassword(
				@Body SetPasswordRequest body,
				Callback<OkResponseHolder> callback);

		@POST("/v1/account/login_password")
		void loginPassword(
				@Body LoginPasswordRequest body,
				Callback<AuthClientVerificationResponseHolder> callback);

		@POST("/v1/account/forgot_password")
		void forgotPassword(
				@Body ForgotPasswordRequest body,
				Callback<OkResponseHolder> callback);

		@POST("/v1/account/reset_password")
		void resetPassword(
				@Body ResetPasswordRequest body,
				Callback<AuthClientVerificationResponseHolder> callback);

		@POST("/v1/account/resend_activation")
		void resendActivation(
				@Body ResendActivationRequest body,
				Callback<OkResponseHolder> callback);

		// WhatsApp registration: request a short token + wa_link to deep-link
		// into WhatsApp. The user isn't logged in yet, so push_token is passed
		// in the body so the backend can push {type:"wa_verified"}.
		@POST("/v1/account/whatsapp_request/")
		void whatsappRequest(
				@Body WhatsappRequest body,
				Callback<WhatsappRequestResponseHolder> callback);

		// Poll for the result of the WhatsApp verification using the SHORT token
		// from whatsapp_request. Returns {verified:false} until the message is
		// matched, then the full account + token (= api_key).
		@POST("/v1/account/whatsapp_status/")
		void whatsappStatus(
				@Body WhatsappStatusRequest body,
				Callback<WhatsappStatusResponseHolder> callback);

		@POST("/v1/account/register")
		void authClientRegistration(
				@Body AuthClientRegistrationRequest body,
				Callback<AuthClientRegistrationResponseHolder> callback);

		@POST("/v1/account/activate/")
		void authClientVerify(
				@Body AuthVerificationRequest body,
				Callback<AuthClientVerificationResponseHolder> callback);
		
		@POST("/v1/account/register")
		void authAdminRegistration(
				@Body AuthAdminRegistrationRequest body,
				Callback<AuthAdminRegistrationResponseHolder> callback);

		@GET("/v1/account/{id}")
		void userGet(
				@Path("id") String userId,
				Callback<UserResponseHolder> callback);
		
		@PATCH("/v1/account/{id}")
		void userUpdate(
				@Path("id") String userId,
				@Body UserUpdateRequest body,
				Callback<UserResponseHolder> callback);		
		
		@POST("/v1/image_upload/default/{id}")
		void userPhotoChange(
				@Path("id") String id,
				Callback<BaseResponseHolder> callback);
		
		
		@POST("/v1/account/logout")
		void authLogout(
				Callback<BaseResponseHolder> callback);

		@GET("/v1/client_bookmarks")
		void washersSearchFavourite(
				@QueryMap Map<String, String> options,
				Callback<WashersResponseHolder> callback);

		@GET("/v1/public_carwash")
		void washersSearchAvailable(
				@QueryMap Map<String, String> options,
				Callback<WashersResponseHolder> callback);

		@GET("/v1/public_carwash")
		void washersSearchPromo(
				@QueryMap Map<String, String> options,
				Callback<WashersResponseHolder> callback);

		@GET("/v1/public_carwash")
		void washersSearchNearBy(
				@QueryMap Map<String, String> options,
				Callback<WashersResponseHolder> callback);

		@GET("/v1/map_carwash")
		void washersOnMap(
				@QueryMap Map<String, String> options,
				Callback<WashersResponseHolder> callback);

		@GET("/v1/public_carwash")
		void washerCampaigns(
				@QueryMap Map<String, String> options,
				Callback<WashersResponseHolder> callback);

		@GET("/v1/public_carwash/{washerid}")
		void washerInfo(
				@Path("washerid") String washerId,
				Callback<WasherResponseHolder> callback);

		@GET("/v1/public_carwash/timetable")
		void washerTimeTable(
				@Query("carwash") String washerId,
				@Query("day") String day,
				Callback<WasherTimeTableResponseHolder> callback);

		@GET("/v1/company_booking/timetable")
		void washerTimeTableWithBook(
				@Query("carwash") String washerId,
				@Query("day") String day,
				Callback<WasherTimeTableResponseHolder> callback);

		@POST("/v1/client_booking")
		void bookRequest(
				@Body BookingRequest body,
				Callback<BookResponseHolder> callback);

		@GET("/v1/client_booking/{bookid}")
		void bookingInfo(
				@Path("bookid") String bookId,
				Callback<BookInfoResponseHolder> callback);
		
		@GET("/v1/client_booking")
		void booksList(
				@QueryMap Map<String, String> options,
				Callback<BooksResponseHolder> callback);
		
		@GET("/v1/client_notifications")
		void notificationList(
				@QueryMap Map<String, String> options,
				Callback<NotificationResponseHolder> callback);

		@POST("/v1/client_booking/cancel")
		void bookingCancel(
				@Body BookingRequest body,
				Callback<BookResponseHolder> callback);		
		
		@POST("/v1/client_bookmarks")
		void addToFavourite(
				@Body WasherRequest body,
				Callback<WasherResponseHolder> callback);

		@DELETE("/v1/client_bookmarks/{washerid}")
		void removeFromFavourite(
				@Path("washerid") String washerId,
				Callback<BaseResponseHolder> callback);
		
		@Multipart
		@POST("/v1/image_upload/new")
		void clientPhotoPost(
				@Part("image") TypedFile photo,
				Callback<PhotoResponseHolder> callback);

		@POST("/v1/image_upload/delete/{photoid}")
		void clientPhotoDelete(
				@Path("photoid") String photoId,
				Callback<BaseResponseHolder> callback);

		@POST("/v1/client_reviews")
		void addClientReview(
				@Body AddReviewRequest body,
				Callback<BaseResponseHolder> callback);
		
		///////Admin part starting\\\\\\\\
		
		@POST("/v1/account/activate/")
		void authAdminVerify(
				@Body AuthVerificationRequest body,
				Callback<AuthAdminVerificationResponseHolder> callback);
		
		@GET("/v1/company_carwash")
		void  getOwnWashers(
				@QueryMap Map<String, String> options,
				Callback<WashersResponseHolder> callback);
		
		@POST("/v1/company_carwash")
		void createInitialWasher(
				@Body InitialWasherCreateRequest body,
				Callback<WasherResponseHolder> callback);
		
		@Multipart
		@POST("/v1/image_upload/new")
		void adminPhotoPost(
				@Part("image") TypedFile photo,
				@Part("carwash") String washerId,
				Callback<PhotoResponseHolder> callback);
		
		@GET("/v1/company_carwash/{washerid}")
		void adminWasherInfo(
				@Path("washerid") String washerId,
				Callback<WasherResponseHolder> callback);
		
		@PATCH("/v1/company_carwash/{washerid}")
		void editWasher(
				@Path("washerid") String washerId,
				@Body WasherBase body,
				Callback<WasherResponseHolder> callback);
		
		@GET("/v1/company_booking")
		void companyBooksList(
				@QueryMap Map<String, String> options,
				Callback<BooksResponseHolder> callback);

		@GET("/v1/company_booking/queued/")
		void companyQueuedBooksList(
				@QueryMap Map<String, String> options,
				Callback<QueuedBooksResponseHolder> callback);

		@GET("/v1/company_booking/statistics/")
		void companyStatisticsList(
				@QueryMap Map<String, String> options,
				Callback<QueuedBooksResponseHolder> callback);

		@POST("/v1/company_booking")
		void companyBookRequest(
				@Body BookingRequest body,
				Callback<BookResponseHolder> callback);

		@GET("/v1/company_booking/{bookid}")
		void companyBookingInfo(
				@Path("bookid") String bookId,
				Callback<BookInfoResponseHolder> callback);

		@POST("/v1/company_booking/start_queue")
		void startQueueRequest(
				@Body BookingRequest body,
				Callback<BookResponseHolder> callback);

		@POST("/v1/company_booking/finish_queue")
		void finishQueueRequest(
				@Body BookingRequest body,
				Callback<BookResponseHolder> callback);

		@POST("/v1/company_booking/postpone_queue")
		void postponeQueueRequest(
				@Body BookingRequest body,
				Callback<BookResponseHolder> callback);

		@POST("/v1/company_booking/client_paid")
		void markAsClientPaidRequest(
				@Body BookingRequest body,
				Callback<BookResponseHolder> callback);

		@POST("/v1/company_booking/reject_queue")
		void rejectQueueRequest(
				@Body BookingRequest body,
				Callback<BookResponseHolder> callback);

		@POST("/v1/company_booking/reject")
		void adminBookingCancel(
				@Body BookingRequest body,
				Callback<BookResponseHolder> callback);
		
		@POST("/v1/company_booking/approve")
		void adminBookingApprove(
				@Body BookingRequest body,
				Callback<BookResponseHolder> callback);		

		@POST("/v1/company_booking/start")
		void adminBookingStart(
				@Body BookingRequest body,
				Callback<BookResponseHolder> callback);
		
		@POST("/v1/company_booking/finish")
		void adminBookingStop(
				@Body BookingRequest body,
				Callback<BookResponseHolder> callback);		

		@POST("/v1/company_booking/nocome")
		void adminBookingNoClient(
				@Body BookingRequest body,
				Callback<BookResponseHolder> callback);		

		@POST("/v1/company_booking/fail")
		void adminBookingFail(
				@Body BookingRequest body,
				Callback<BookResponseHolder> callback);	
		
		@GET("/v1/company_carwash/{washerid}/box")
		void adminBoxList(
				@Path("washerid") String washerId,
				Callback<AdminBoxesResponseHolder> callback);
		
		@GET("/v1/company_carwash/{washerid}/box/{boxid}")
		void adminBox(
				@Path("washerid") String washerId,
				@Path("boxid") String boxId,
				Callback<AdminBoxResponseHolder> callback);
		
		@PATCH("/v1/company_carwash/{washerid}/box/{boxid}")
		void adminChangeBox(
				@Path("washerid") String washerId,
				@Path("boxid") String boxId,
				@Body BoxOperationRequest body,
				Callback<AdminBoxResponseHolder> callback);

		@POST("/v1/company_carwash/{washerid}/box/")
		void adminAddBox(
				@Path("washerid") String washerId,
				@Body BoxOperationRequest body,
				Callback<AdminBoxResponseHolder> callback);

		@DELETE("/v1/company_carwash/{washerid}/box/{boxid}")
		void adminDeleteBox(
				@Path("washerid") String washerId,
				@Path("boxid") String boxId,
				Callback<AdminBoxResponseHolder> callback);

		@POST("/v1/company_carwash")
		void createTmpWasher(
				@Body Washer body,
				Callback<WasherResponseHolder> callback);

		@GET("/v1/settings")
		void settingsList(
				Callback<BaseResponseHolder> callback);

		@POST("/v1/settings")
		void updateSettings(
				@Body Settings body,
				Callback<BaseResponseHolder> callback);

		@PUT("/v1/location/{locationText}")
		void updateLocation(
				@Path("locationText") String locationText,
				@Body UserLocation body,
				Callback<BaseResponseHolder> callback);

		@GET("/v1/devices/{deviceId}")
		void getDevice(
                String deviceId,
				Callback<BaseResponseHolder> callback);

		@PUT("/v1/devices/{deviceId}")
		void addDevice(
				@Path("deviceId") String deviceId,
				@Body Device body,
				Callback<BaseResponseHolder> callback);

		@DELETE("/v1/devices/{deviceId}")
		void deleteDevice(
				@Path("deviceId") String deviceId,
				Callback<BaseResponseHolder> callback);

		@GET("/v1/client_clients/single/")
		void clientSingleClient(
				@QueryMap Map<String, String> options,
				Callback<ClientResponseHolder> callback);

		@POST("/v1/client_request")
		void clientFareRequest(
				@Body FareRequest body,
				Callback<FareRequestResponseHolder> callback);

		@GET("/v1/client_request")
		void clientFareRequests(
				@Query("status") String status,
				Callback<FareRequestsResponseHolder> callback);

		@POST("/v1/client_request/cancel")
		void fareRequestCancel(
				@Body FareRequest body,
				Callback<FareRequestResponseHolder> callback);

		@POST("/v1/client_request/increase_fare")
		void fareRequestIncreaseFare(
				@Body FareRequest body,
				Callback<FareRequestResponseHolder> callback);

		@POST("/v1/client_request/accept_bid")
		void fareRequestAcceptBidFare(
				@Body FareRequest body,
				Callback<FareRequestResponseHolder> callback);

		@POST("/v1/client_request/bid_not_accepted")
		void fareRequestNotAcceptBidFare(
				@Body FareRequest body,
				Callback<FareRequestResponseHolder> callback);

   /*
		@POST("/v1/client_bookmarks")
		void addToFavourite(
				@Body WasherRequest body,
				Callback<WasherResponseHolder> callback);

		@DELETE("/v1/client_bookmarks/{washerid}")
		void removeFromFavourite(
				@Path("washerid") String washerId,
				Callback<BaseResponseHolder> callback);
     */
	}
	
	public static WashmeApi getRestAdapterNoHeader() {
		return getRestAdapterGeneral(false, false, false);
	}
	
	/**
	 * General rest adapter for connecting to REST services
	 * @return - api for connecting to REST services
	 */
	public static WashmeApi getRestAdapter() {
		return getRestAdapterGeneral(true, false, false);
	}
	
	public static WashmeApi getRestAdapterWithBody() {
		return getRestAdapterGeneral(true, true, false);
	}
	
	public static WashmeApi getRestAdapterForUpload() {
		return getRestAdapterGeneral(true, true, true);
	}

	/**
	 * Request adapter
	 * @param withBody - whether to add content type or not
	 * @return - api for connecting to REST services
	 */
	private static WashmeApi getRestAdapterGeneral(final boolean accept, final boolean withBody, final boolean forUpload) {
		RequestInterceptor requestInterceptor = new RequestInterceptor() {
			@Override
			public void intercept(RequestFacade request) {
				// if there is a user token, add that to request
			String token = UserPreferences.getUserToken(BA.getContext());
			// Send the selected UI language on every request so the backend
			// localizes responses (references, messages) accordingly.
			String lang = UserPreferences.getUserLocale(BA.getContext());
			if (TextUtils.isEmpty(lang)) {
				lang = LocaleManager.backendCode(LocaleManager.current());
			}
			request.addHeader("Accept-Language", lang);
			request.addHeader("Content-Language", lang);
			if( !TextUtils.isEmpty(token) ){
				request.addHeader("Authorization", "ApiKey "+token);
			}
			}
		};

		Executor executor = Executors.newCachedThreadPool();
		RestAdapter restAdapter = new RestAdapter.Builder()
		.setEndpoint(API_URL)
		.setRequestInterceptor(requestInterceptor)
		.setConverter(new GsonConverter(Converters.gsonWithDate()))
		.setExecutors(executor, executor)		
		.setClient(new OkClient(BA.getHttpClient()))
		.setLogLevel(RestAdapter.LogLevel.FULL)
//		.setLogLevel(RestAdapter.LogLevel.NONE)
		.build();

		return restAdapter.create(WashmeApi.class);
	}
}