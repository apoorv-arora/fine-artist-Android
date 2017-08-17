package in.fine.artist.home.utils.networking;

/**
 * Created by nik on 1/10/16.
 */
public interface UploadManagerCallback {
    void uploadStarted(int requestType, Object data, Object requestData);
    void uploadFinished(int requestType, Object data, boolean status, String errorMessage, Object requestData);
}
