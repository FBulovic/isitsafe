package za.org.droidika.lazy;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class SafeLazy {
	private static class SingletonHolder {
		static DefaultHttpClient client = new DefaultHttpClient();
		static HttpParams params = new BasicHttpParams();
		static {
			System.out.println("in static");
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, "utf-8");
			params.setBooleanParameter("http.protocol.expect-continue", false);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			final SSLSocketFactory sslSocketFactory = SSLSocketFactory
					.getSocketFactory();
			sslSocketFactory
					.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			registry.register(new Scheme("https", sslSocketFactory, 443));
			ClientConnectionManager manager = new ThreadSafeClientConnManager(
					params, registry);
			ConnManagerParams.setMaxTotalConnections(params, 20);
			ConnManagerParams.setMaxConnectionsPerRoute(params,
					new ConnPerRoute() {
						public int getMaxForRoute(HttpRoute httproute) {
							return 10;
						}
					});
			client = new DefaultHttpClient(manager, params);
		}
	}

	public static DefaultHttpClient getInstance() {
		System.out.println("in getInstance");
		return SingletonHolder.client;
	}
}
