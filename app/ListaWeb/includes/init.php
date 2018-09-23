<?php

require_once '../includes/DbOperations.php'; 
$language = 'en';
$allawed_lang = Array('en','fr','ar');

if ($_SERVER['REQUEST_METHOD']=='POST' && isset($_POST['language']) && in_array($_POST['language'], $allawed_lang)) {
	$language = $_POST['language'];
}
require_once '../lang/'.$language.'.php';

?>