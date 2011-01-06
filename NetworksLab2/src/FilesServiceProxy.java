import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;


public class FilesServiceProxy extends ProxyBase implements IFileService 
{

	public FilesServiceProxy(String targetIP, int targetPort) 
	{
		super(targetIP, targetPort);
	}
	
	public String GetSharedFiles() throws UnknownHostException, IOException, HttpHeaderParsingException, HttpResponseParsingException, HttpProxyException
	{
		HttpRequestParser request = GetRequest(HttpRequestMethod.GET, "/files_service/get_shared_files");
		
		HttpResponseParser response = SendData(request);
		
		// validate response
		if (!response.GetHttpResponseCodeObject().equals(HttpResponseCode.OK))
		{
			throw new HttpProxyException(String.format("Destination reported an error during add_me_as_a_friend: %s", response.GetHttpResponseCode()));
		}
		
		return new String(response.GetContent());
	}
	
	public String DownloadFile(String fileName) throws UnknownHostException, IOException, HttpHeaderParsingException, HttpResponseParsingException, HttpProxyException
	{
		HttpRequestParser request = GetRequest(HttpRequestMethod.GET, "/files_service/download_file", fileName);
		
		HttpResponseParser response = SendData(request);
		
		// validate response
		if (!response.GetHttpResponseCodeObject().equals(HttpResponseCode.OK))
		{
			throw new HttpProxyException(String.format("Destination reported an error during add_me_as_a_friend: %s", response.GetHttpResponseCode()));
		}
		byte[] decodedFile = Base64Coder.decode(new String(response.GetContent()));
		// TODO remove the temp!!
		String fileNameForSaving = String.format("%stemp\\%s", FilesService.get_instance().GetRootDir(), fileName);
		
		Lab2Utils.WriteFile(decodedFile, fileNameForSaving);
		
		return new String();
	}
}
