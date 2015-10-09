package Chinachu4j;

public class ChinachuResponse{

	private int responseCode;
	private String message;
	private boolean result;

	public ChinachuResponse(int responseCode){
		this.responseCode = responseCode;
		result = false;
		setMessage();
	}

	private void setMessage(){
		switch(responseCode){
		case 200:
			message = "リクエストは成功し、正常に処理されました。";
			result = true;
			break;
		case 202:
			message = "リクエストは受理されました。処理は続行されます。";
			result = true;
			break;
		case 400:
			message = "不正なリクエストです。";
			break;
		case 401:
			message = "認証が必要です。";
			break;
		case 403:
			message = "リクエストの実行が拒否されました。";
			break;
		case 404:
			message = "リクエストされたAPIまたはリソースが見つかりませんでした。";
			break;
		case 405:
			message = "許可されていないメソッドでリクエストしました。";
			break;
		case 409:
			message = "リクエストに関して何らかの衝突が発生しました。";
			break;
		case 410:
			message = "あるはずのリソースが、行方不明になりました。";
			break;
		case 415:
			message = "指定されたレスポンスフォーマットが有効でない可能性があります。";
			break;
		case 500:
			message = "内部エラーが発生しました。";
			break;
		case 501:
			message = "実装または有効になっていないリソースまたは機能をリクエストしました。";
			break;
		case 503:
			message = "過負荷状態またはメンテナンス状態であるためリクエストが続行できませんでした。";
			break;
		default:
			message = "不明なエラーが発生しました。";
			break;
		}
	}

	public int getResponseCode(){
		return responseCode;
	}

	public String getMessage(){
		return message;
	}

	public boolean getResult(){
		return result;
	}
}
