import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestAlmaAPI {
	public static void main(String args[]) {
		try {

			URL url = new URL(
					// Course URL: "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=l8xx41bf077192274811b545c60ba48df465");
					"https://api-ap.hosted.exlibrisgroup.com/almaws/v1/acq/po-lines?apikey=l8xx41bf077192274811b545c60ba48df465");
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("Content-Type", "application/json");

			//Course String: String data = "{\"link\":\"\",\"code\":\"C_CODE\",\"name\":\"C_NAME\",\"section\":\"01\",\"academic_department\":{\"value\":\"C_DEPT\"},\"processing_department\":{\"value\":\"PROSS_DEPT\"},\"term\":[{\"value\":\"C_TERM\"}],\"status\":\"C_STATUS\",\"start_date\":\"C_START\",\"end_date\":\"C_END\",\"weekly_hours\":\"0\",\"participants\":\"0\",\"year\":\"C_YEAR\",\"instructor\":[{\"primary_id\":\"C_INSTRUCTOR\"}],\"campus\":[{\"campus_code\":{\"value\":\"code\"},\"campus_participants\":\"0\"}],\"submit_by_date\":\"C_SDATE\"}";
			
			String data = "{\"link\":\"\",\"owner\":{\"value\":\"ACQEN\"},\"type\":{\"value\":\"PRINTED_BOOK_OT\"},\"vendor\":{\"value\":\"MVEN\"},\"vendor_account\":\"MVEN\",\"reclaim_interval\":\"1\",\"expected_receipt_interval\":\"1\",\"claiming_interval\":\"1\",\"expected_activation_interval\":\"1\",\"subscription_interval\":\"1\",\"expected_activation_date\":\"2015-05-02Z\",\"e_activation_due_interval\":\"1\",\"acquisition_method\":{\"value\":\"VENDOR_SYSTEM\"},\"no_charge\":\"false\",\"rush\":\"false\",\"cancellation_restriction\":\"false\",\"cancellation_restriction_note\":\"\",\"price\":{\"sum\":\"150.0\",\"currency\":{\"value\":\"USD\"}},\"discount\":\"20\",\"vendor_reference_number\":\"125\",\"vendor_reference_number_type\":{\"value\":\"SCO\"},\"source_type\":{\"value\":\"SELECTIONITEM\"},\"po_number\":\"PO-254153\",\"additional_order_reference\":\"Order1234\",\"invoice_reference\":\"\",\"resource_metadata\":{\"mms_id\":{\"value\":\"990000912760108061\"},\"title\":\"Worldwithoutend/KenFollett.\",\"author\":\"Follett,Ken.\",\"issn\":\"\",\"isbn\":\"2007026639\",\"publisher\":\"Dutton,\",\"publication_place\":\"NewYork:\",\"publication_year\":\"c2007.\",\"vendor_title_number\":\"\",\"title_id\":\"12345\",\"system_control_number\":[\"\"]},\"fund_distribution\":[{\"fund_code\":{\"value\":\"00LIS\"},\"amount\":{\"sum\":\"150.0\",\"currency\":{\"value\":\"USD\"}}}],\"reporting_code\":\"b\",\"secondary_reporting_code\":\"sso\",\"tertiary_reporting_code\":\"u\",\"fourth_reporting_code\":\"u\",\"fifth_reporting_code\":\"u\",\"vendor_note\":\"\",\"receiving_note\":\"\",\"note\":[{\"note_text\":\"Createdby:JohnSmith\"}],\"location\":[{\"quantity\":\"1\",\"library\":{\"value\":\"LAW\"},\"shelving_location\":\"LAWPER\",\"copy\":[{\"link\":\"\",\"barcode\":\"2379499752\",\"item_policy\":{\"value\":\"09\"},\"receive_date\":\"2012-12-12Z\",\"enumeration_a\":\"\",\"enumeration_b\":\"\",\"enumeration_c\":\"\",\"chronology_i\":\"\",\"chronology_j\":\"\",\"chronology_k\":\"\",\"description\":\"desc\",\"storage_location_id\":\"12339700001021\",\"is_temp_location\":\"false\",\"permanent_library\":{\"value\":\"\"},\"permanent_shelving_location\":\"\"}]}],\"interested_user\":[{\"primary_id\":\"wwyng\",\"notify_receiving_activation\":\"false\",\"hold_item\":\"false\",\"notify_renewal\":\"false\",\"notify_cancel\":\"false\"}],\"license\":{\"value\":\"\"},\"access_model\":\"unlimited\",\"url\":\"\",\"base_status\":\"ACTIVE\",\"access_provider\":\"\",\"manual_renewal\":\"false\",\"renewal_cycle\":{\"value\":\"1\"},\"subscription_from_date\":\"2015-05-02Z\",\"subscription_to_date\":\"2018-05-02Z\",\"renewal_date\":\"2018-05-03Z\",\"renewal_period\":\"100\",\"renewal_note\":\"\",\"material_type\":{\"value\":\"BOOK\"},\"expected_receipt_date\":\"2023-08-01Z\"}";

			byte[] out = data.getBytes(StandardCharsets.UTF_8);

			OutputStream stream = http.getOutputStream();
			stream.write(out);

			BufferedReader br = null;
			if (100 <= http.getResponseCode() && http.getResponseCode() <= 399) {
				br = new BufferedReader(new InputStreamReader(http.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(http.getErrorStream()));
			}

			System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
			System.out.println(br.readLine());

			http.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
