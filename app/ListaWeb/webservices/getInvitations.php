<?php
 
require_once '../includes/init.php'; 

$response = array();

if ($_SERVER['REQUEST_METHOD']=='POST'){
		
	if (isset($_POST['co_user_email']) ) {	
			
		$co_user_email = $_POST['co_user_email'];
		
		$db = new DbOperations();
		
		$invitations = array();
		$invitations = $db->getInvitations($co_user_email);
		

		$db->disconnect();
		
		$response['error'] = false;		
		$response['invitations'] = $invitations;
		
		
					
	} else {
		$response['error'] = true;
		$response['message'] = $lang['required_fields'];
	}
		
		
} else {
	$response['error'] = true;
	$response['message'] = $lang['invalid_request'];
}

echo json_encode($response);
 
?>