<?php

	Class DbOperations {
		
		private $con;
		
		private $ERROR = 0;
		private $SUCCESS = 1;
		private $EXISTING_ITEM = 2;
		
		private $SYNC_STATUS_OK = 1;
		private $SYNC_STATUS_FAILED = 0;
		
		private $ACCEPTED = 1;
		private $REJECTED = -1;
		
		private $LISTA = 'lista';
		
		public $HAS_RESPONDED_NO = 0;
		public $HAS_RESPONDED_YES = 1;
		
		function __construct(){
			require_once dirname(__FILE__).'/DbConnect.php';		
			$db = new DbConnect();
			$this->con = $db->connect();
		}
		
		public function disconnect(){
			if (!mysqli_close($this->con)){
				echo "Failed to disconnect the database";
			};
		}
		
		// A - public functions
		
		// A - 1 - Reanding functions
		
		public function getdefaultCategories(){
		
			$sql = "SELECT cat.`category_name`, cat.crud_status
					FROM `categories` as cat, users as u
					WHERE cat.server_user_id = u.server_user_id
					AND u.server_user_id = 1";

			$results = mysqli_query($this->con, $sql);
			
			$categories = array();
			while ($row = mysqli_fetch_array($results)){
				
				$category = array();
				
				$category['category_name'] = $row[0];
				$category['crud_status'] = $row[1];
				
				array_push($categories, $category);
	
			}
			
			return $categories;
		}
		
		public function getdefaultGoods(){
					
			$sql = "SELECT g.`good_name`, g.`quantity_level`,
							g.`is_to_buy`, g.crud_status
					FROM`goods` as g
					WHERE g.server_originator_id = 1";

			$results = mysqli_query($this->con, $sql);
			
			$goods = array();
			while ($row = mysqli_fetch_array($results)){
				
				$good = array();

				$good['good_name'] = $row[0];
				$good['quantity_level'] = $row[1];
				$good['is_to_buy'] = $row[2];			
				$good['crud_status'] = $row[3];			
				
				array_push($goods, $good);
	
			}
			
			return $goods;
		}
		
		public function getGroupByServerCoUserUserId($serverCoUserUserId){
			$stmt = $this->con->prepare("SELECT gr.`server_group_id`, gr.`group_name`, gr.`owner_email`,
												gr.`server_owner_id`, gr.`sync_status`
										FROM `groups` as gr, users as u, co_users as co
										WHERE co.server_user_id = u.server_user_id
										AND gr.server_group_id = u.server_group_id
										AND co.server_co_user_id = ?");
			$stmt->bind_param("s",$serverCoUserUserId);
			$stmt->execute();
			$stmt->bind_result($serverGroupId, $groupName, $ownerEmail , $serverOwnerId , $sync);
			$stmt->fetch();
			$group = array();
			
			$group['server_group_id'] = $serverGroupId;
			$group['group_name'] = $groupName;
			$group['owner_email'] = $ownerEmail;
			$group['server_owner_id'] = $serverOwnerId;
			$group['sync_status'] = $sync;
			
			return $group;
		}
		
		public function getUpdateAvailableByUserId($serverUserId){
			$stmt = $this->con->prepare("SELECT update_available FROM `users` WHERE `server_user_id` = ?");
			$stmt->bind_param("s",$serverUserId);
			$stmt->execute();
			$stmt->bind_result($update_available);
			$stmt->fetch();
			return $update_available;
		}
	
		
		public function getGroupIdByOwnerId($serverOwnerId){
			$stmt = $this->con->prepare("SELECT server_group_id FROM `groups` WHERE `server_owner_id` = ?");
			$stmt->bind_param("s",$serverOwnerId);
			$stmt->execute();
			$stmt->bind_result($server_group_id);
			$stmt->fetch();
			return $server_group_id;
		}
		
		public function getGroupIdByUserId($serverUserId){
			$stmt = $this->con->prepare("SELECT server_group_id FROM `users` WHERE `server_user_id` = ?");
			$stmt->bind_param("s",$serverUserId);
			$stmt->execute();
			$stmt->bind_result($server_group_id);
			$stmt->fetch();
			return $server_group_id;
		}
		
		public function getServerGoodIdByOriginatorIdAndDeviceId($serverOriginatorId, $deviceGoodId){
			$stmt = $this->con->prepare("SELECT server_good_id FROM `goods` as g
											WHERE g.`server_originator_id` = ?
											AND g.device_good_id =?");
			$stmt->bind_param("ss",$serverOriginatorId,$deviceGoodId);
			$stmt->execute();
			$stmt->bind_result($server_good_id);
			$stmt->fetch();
			return $server_good_id;
		}
		
		public function getTeamMembers($currentServerUserId){
		
			$serverGroupId = $this->getGroupIdByUserId($currentServerUserId);
					
			$sql = "SELECT `server_user_id`, `email`, `user_name`, `server_group_id`
					FROM `users`
					WHERE server_group_id = " . $serverGroupId;

			$results = mysqli_query($this->con, $sql);
			
			$users = array();
			while ($row = mysqli_fetch_array($results)){
				
				$user = array();
				
				$user['server_user_id'] = $row[0];
				$user['email'] = $row[1];
				$user['user_name'] = $row[2];
				$user['server_group_id'] = $row[3];
		
				array_push($users, $user);
	
			}
			
			return $users;
		}
		
		
		public function getShops(){
					
			$sql = "SELECT sh.`server_shop_id`, sh.`shop_name`, sh.`shop_adress`, sh.`shop_email`,
					sh.`shop_phone`, sh.`longitude`, sh.`latitude`, sh.`server_shop_type_id`,
					st.shop_type_name, sh.`server_city_id`, ci.city_name, sh.`server_country_id`,
					cou.country_name
					FROM `shops`as sh, cities as ci, countries as cou, shop_types as st
					WHERE sh.server_city_id = ci.server_city_id
					AND sh.server_country_id = cou.server_country_id
                    AND sh.server_shop_type_id = st.server_shop_type_id";

			$results = mysqli_query($this->con, $sql);
			
			$shops = array();
			$i=0;
			while ($row = mysqli_fetch_array($results)){
				
				$shop = array();
				
				$shop['server_shop_id'] = $row[0];
				$shop['shop_name'] = $row[1];
				$shop['shop_adress'] = $row[2];
				$shop['shop_email'] = $row[3];
				$shop['shop_phone'] = $row[4];
				$shop['longitude'] = $row[5];
				$shop['latitude'] = $row[6];
				$shop['server_shop_type_id'] = $row[7];
				$shop['shop_type_name'] = $row[8];
				$shop['server_city_id'] = $row[9];
				$shop['city_name'] = $row[10];
				$shop['server_country_id'] = $row[11];
				$shop['country_name'] = $row[12];
				
				array_push($shops, $shop);
			}
			
			return $shops;
		}
		
		public function getCities(){
					
			$sql = "SELECT ci.`server_city_id`, ci.`city_name`, ci.`server_country_id`, co.country_name
					FROM `cities` as ci, countries as co
					WHERE ci.server_country_id = co.server_country_id
					ORDER BY ci.`server_city_id`";

			$results = mysqli_query($this->con, $sql);
			
			$cities = array();
			$i=0;
			while ($row = mysqli_fetch_array($results)){
				
				$city = array();
				
				$city['server_city_id'] = $row[0];
				$city['city_name'] = $row[1];
				$city['server_country_id'] = $row[2];
				$city['country_name'] = $row[3];
				
				array_push($cities, $city);
			}
			
			return $cities;
		}
		
		
		
		
		public function getUpdatedCategoriesFromServer($currentServerUserId){
		
			$serverGroupId = $this->getGroupIdByUserId($currentServerUserId);
					
			$sql = "SELECT cat.`server_category_id`, cat.`category_name`, cat.`category_color`,
							cat.`category_icon`, cat.`sync_status`, cat.`email`,
							cat.`device_category_id`, cat.`server_user_id`, cat.`crud_status`
					FROM `categories` as cat, users as u
					WHERE cat.server_user_id = u.server_user_id
					AND u.server_group_id = ".$serverGroupId." 
					AND cat.last_update_user_id <> ".$currentServerUserId." 
					AND cat.last_update_user_id > 0";

			$results = mysqli_query($this->con, $sql);
			
			$categories = array();
			while ($row = mysqli_fetch_array($results)){
				
				$category = array();
				
				$category['server_category_id'] = $row[0];
				$category['category_name'] = $row[1];
				$category['category_color'] = $row[2];
				$category['category_icon'] = $row[3];
				$category['sync_status'] = $row[4];
				$category['email'] = $row[5];
				$category['device_category_id'] = $row[6];
				$category['server_user_id'] = $row[7];
				$category['crud_status'] = $row[8];
				
				array_push($categories, $category);
	
			}
			
			return $categories;
		}

		public function getUpdatedGoodsFromServer($currentServerUserId){
		
			$serverGroupId = $this->getGroupIdByUserId($currentServerUserId);
					
			$sql = "SELECT g.`server_good_id`, g.`good_name`, g.`quantity_level`,
							g.`is_to_buy`, g.`sync_status`, g.`email`, g.`server_category_id`, g.`crud_status`
					FROM`goods` as g, categories as cat, users as u
					WHERE g.server_category_id = cat.server_category_id
					AND cat.server_user_id = u.server_user_id
					AND u.server_group_id = ".$serverGroupId." 
					AND g.last_update_user_id <> ".$currentServerUserId."
					AND g.last_update_user_id > 0";

			$results = mysqli_query($this->con, $sql);
			
			$goods = array();
			while ($row = mysqli_fetch_array($results)){
				
				$good = array();
				
				$good['server_good_id'] = $row[0];
				$good['good_name'] = $row[1];
				$good['quantity_level'] = $row[2];
				$good['is_to_buy'] = $row[3];
				$good['sync_status'] = $row[4];
				$good['email'] = $row[5];
				$good['server_category_id'] = $row[6];			
				$good['crud_status'] = $row[7];			
				
				array_push($goods, $good);
	
			}
			
			return $goods;
		}
		
		
		public function getGroupCategories($server_group_id, $excludedCategoriesServerIds, $currentServerUserId){
					
			$sql_not_in = "";
			if (count($excludedCategoriesServerIds)>0){
				$sql_not_in = " AND cat.server_category_id NOT IN (" . implode( " , " , $excludedCategoriesServerIds ) . " )";
			}
			
			$sql = "SELECT cat.server_category_id, cat.category_name, cat.category_color, cat.category_icon,
								cat.sync_status, cat.email, cat.device_category_id, cat.server_user_id, cat.crud_status
					FROM categories as cat, users as u
					WHERE u.server_user_id = cat.server_user_id
					AND u.server_user_id <> " . $currentServerUserId ."
					AND	u.server_group_id = " . $server_group_id . $sql_not_in;

			$results = mysqli_query($this->con, $sql);
			
			$categories = array();
			while ($row = mysqli_fetch_array($results)){
				
				$category = array();
				
				array_push($category, array("server_category_id"=>$row[0]));
				array_push($category, array("category_name"=>$row[1]));
				array_push($category, array("category_color"=>$row[2]));
				array_push($category, array("category_icon"=>$row[3]));
				array_push($category, array("sync_status"=>$row[4]));
				array_push($category, array("email"=>$row[5]));
				array_push($category, array("device_category_id"=>$row[6]));
				array_push($category, array("server_user_id"=>$row[7]));
				array_push($category, array("crud_status"=>$row[8]));

				array_push($categories, $category);
	
			}
			
			return $categories;
		}
		
		public function getGroupGoods($server_group_id, $excludedGoodsServerIds, $currentServerUserId){
		
		
			$sql_not_in = "";
			if (count($excludedGoodsServerIds)>0){
				$sql_not_in = " AND g.server_good_id NOT IN (" . implode( " , " , $excludedGoodsServerIds ) . " )";
			}
			
			$sql = "SELECT g.server_good_id, g.good_name, g.quantity_level, g.is_to_buy, g.sync_status, 
							g.device_good_id, g.device_category_id, g.email,
							g.server_category_id, g.crud_status, g.good_desc
					FROM categories as cat, users as u, goods as g
					WHERE u.server_user_id = cat.server_user_id
					AND cat.server_category_id = g.server_category_id
					AND g.server_originator_id <> " . $currentServerUserId ."
					AND	u.server_group_id = " . $server_group_id . $sql_not_in;
					
			$results = mysqli_query($this->con, $sql);
			
			$goods = array();
			while ($row = mysqli_fetch_array($results)){
				
				$good = array();
				
				array_push($good, array("server_good_id"=>$row[0]));
				array_push($good, array("good_name"=>$row[1]));
				array_push($good, array("quantity_level"=>$row[2]));
				array_push($good, array("is_to_buy"=>$row[3]));
				array_push($good, array("sync_status"=>$row[4]));
				array_push($good, array("device_good_id"=>$row[5]));
				array_push($good, array("device_category_id"=>$row[6]));
				array_push($good, array("email"=>$row[7]));
				array_push($good, array("server_category_id"=>$row[8]));
				array_push($good, array("crud_status"=>$row[9]));
				array_push($good, array("good_desc"=>$row[10]));
				
				array_push($goods,  $good);
				
			}
			
			return $goods;
		}
		
		
		public function getInvitations($co_user_email){
			
			$invitations = array();
			
			$sql_emails = "SELECT users.email, co_users.server_co_user_id, users.server_group_id
							FROM co_users, users
							WHERE users.server_user_id = co_users.server_user_id
							AND co_users.has_responded = ".$this->HAS_RESPONDED_NO." 
							AND co_users.co_user_email = '".$co_user_email."'";
			
			$result_emails = mysqli_query($this->con, $sql_emails);

			while ($row = mysqli_fetch_array($result_emails)){
				
				$invitation = array();
				
				$invitation['email'] = $row[0];
				$invitation['server_co_user_id'] = $row[1];
				$invitation['server_group_id'] = $row[2];
				
					$serverGroupId = $row[2];

					$sql_categories = "select categories.category_name FROM users, categories
										WHERE categories.server_user_id = users.server_user_id
										AND users.server_group_id = " . $serverGroupId ;
					
					$categories_names = array();
					
					$result_categories = mysqli_query($this->con, $sql_categories);
							
					while ($row_category = mysqli_fetch_array($result_categories)){

						$categoryName = array();
						
						$categoryName['category_name'] = $row_category[0];
						
						array_push($categories_names, $categoryName);
					}
				
				$invitation['categories_names'] = $categories_names;
				
				array_push($invitations, $invitation);

			}
			return $invitations;
		}

		
		public function loginUser($email, $pass, $signUpType){
			$password = MD5($pass);
			$stmt = $this->con->prepare("SELECT server_user_id FROM users
										WHERE email = ?
										AND password = ?
										AND sign_up_type = ?");
			$stmt->bind_param("sss", $email, $password, $signUpType);
			$stmt->execute();
			$stmt->bind_result($serverUserId);
			$stmt->fetch();
			return $serverUserId;
		}

		public function getServerGoodId($email, $device_good_id){
			$stmt = $this->con->prepare("SELECT server_good_id FROM `goods` WHERE `email` = ? AND `device_good_id` = ?");
			$stmt->bind_param("ss",$email, $device_good_id);
			$stmt->execute();
			$stmt->bind_result($server_good_id);
			$stmt->fetch();
			return $server_good_id;
		}
		
		public function getCategoryIdByServerUserIdAndDeviceCategoryId($server_user_id, $device_category_id){
			$stmt = $this->con->prepare("SELECT server_category_id FROM `categories` WHERE `server_user_id` = ? AND `device_category_id` = ?");
			$stmt->bind_param("ss",$server_user_id, $device_category_id);
			$stmt->execute();
			$stmt->bind_result($server_category_id);
			$stmt->fetch();
			return $server_category_id;
		}
	
		public function getUserIdByEmail($email, $signUpType){
			$stmt = $this->con->prepare("SELECT `server_user_id`, `email`, `password`,
												`user_name`, `server_group_id`,
												`update_available`, `sign_up_type`
											FROM `users`
											WHERE `email` = ? AND sign_up_type = ?");
			$stmt->bind_param("ss",$email, $signUpType);
			$stmt->execute();
			$stmt->bind_result($server_user_id, $email, $password, $user_name,
								$server_group_id, $update_available, $sign_up_type);
			$stmt->fetch();
			return $server_user_id;
		}
		
		public function getServerCoUserId($email, $co_user_email){
			$stmt = $this->con->prepare("SELECT server_co_user_id FROM `co_users` WHERE `email` = ? AND `co_user_email` = ?");
			$stmt->bind_param("ss", $email, $co_user_email);
			$stmt->execute();
			$stmt->bind_result($server_co_user_id);
			$stmt->fetch();
			return $server_co_user_id;
		}
		
		public function getCatgeoryIds($email, $device_category_id){
			$stmt = $this->con->prepare("SELECT server_category_id, server_user_id FROM `categories`
										WHERE `email` = ? AND `device_category_id` = ?");
			$stmt->bind_param("ss", $email, $device_category_id);
			$stmt->execute();
			$stmt->bind_result($server_category_id, $server_user_id);
			$stmt->fetch();
			$ids = Array ('server_category_id' => $server_category_id,
							'server_user_id' => $server_user_id);
			return $ids;
		}
		
		public function getServerCoUserIdBydeviceCoUserId($email, $device_co_user_id){
			$stmt = $this->con->prepare("SELECT server_co_user_id FROM `co_users`
										WHERE `email` = ? AND `device_co_user_id` = ?");
			$stmt->bind_param("ss", $email, $device_co_user_id);
			$stmt->execute();
			$stmt->bind_result($server_co_user_id);
			$stmt->fetch();
			return $server_co_user_id;
		}

	
		
		
		//A - 2 - creating functions
				

				
		public function addCoUser($co_user_email, $email, $device_co_user_id, $signUpType){
			
			if ($this->coUserExists($email, $co_user_email)){
				return $this->EXISTING_ITEM;
			} else {
				
				$all_query_Ok = true;
				mysqli_autocommit($this->con,FALSE);
				
				$stmt = $this->con->prepare("INSERT INTO `co_users`
				(`device_co_user_id`, `co_user_email`, `email`, `sync_status`, `server_user_id`)
				VALUES (?,?,?,?,?)");
				
				$server_user_id = $this->getUserIdByEmail($email, $signUpType);				
				$stmt->bind_param("sssss", $device_co_user_id, $co_user_email, $email, $this->SYNC_STATUS_OK, $server_user_id);
				
				
				if (!$stmt->execute()) { 

					$all_query_Ok = false;
				}
				
				if ($all_query_Ok){
					mysqli_commit($this->con);
					return $this->SUCCESS;
				} else {
					//mysqli_rollback($this->con);
					return $this->ERROR;
				}
				
			}
		
		}
				
		public function addCategory($category_name, $category_color, $category_icon, $sync_status, $device_category_id, $email, $server_user_id){

			$all_query_Ok = true;
			mysqli_autocommit($this->con,FALSE);
			
			$stmt = $this->con->prepare("INSERT INTO `categories`
			(`category_name`, `category_color`, `category_icon`, `sync_status`, `device_category_id`, `email`, `server_user_id` )
			VALUES (?, ?, ?, ?, ?, ?, ?);");
			
			$stmt->bind_param("sssssss", $category_name, $category_color, $category_icon,
											$sync_status, $device_category_id, $email, $server_user_id);
			
			if (!$stmt->execute()) { $all_query_Ok = false; }
			
			if ($all_query_Ok){
				mysqli_commit($this->con);
				return $this->SUCCESS;
			} else {
				//mysqli_rollback($this->con);
				return $this->ERROR;
			}

		}
		
		public function addCategories($categories, $serverUserId){

			$all_query_Ok = true;
			mysqli_autocommit($this->con,FALSE);

				foreach ($categories as $category) {
					
					$categoryName = $category['categoryName'];
					$color = $category['color'];
					$icon = $category['icon'];
					$sync = 1;
					$email = $category['email'];
					$deviceCategoryId = $category['categoryId'];
					$crud = $category['crud_status'];
					
					$stmt = $this->con->prepare("INSERT INTO `categories`
												(`category_name`, `category_color`,
												 `category_icon`, `sync_status`, `email`,
												 `device_category_id`, `crud_status`, `server_user_id`)
												VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
				
					$stmt->bind_param("ssssssss", $categoryName, $color, $icon,	$sync,
													$email, $deviceCategoryId, $crud, $serverUserId );
						
					if (!$stmt->execute()) { $all_query_Ok = false; }
							
				}

			if ($all_query_Ok){
				mysqli_commit($this->con);
				return $this->SUCCESS;
			} else {
				mysqli_rollback($this->con);
				return $this->ERROR;
			}

		}
		
		public function addGoods($goods, $serverUserId){

			$all_query_Ok = true;
			mysqli_autocommit($this->con,FALSE);

			foreach ($goods as $good) {
				
				$goodName = $good['goodName'];
				$goodDesc = $good['goodDesc'];
				$quantityLevelId = $good['quantityLevelId'];
				$isToBuy = $good['isToBuy'];
				$sync = 1;
				$deviceGoodId = $good['goodId'];
				$deviceCategoryId = $good['categoryId'];
				$email = $good['email'];
				$crud = $good['crud_status'];
				$serverCategoryId = $good['serverCategoryId'];
				$serverOriginatorId = $serverUserId;

				
				$stmt = $this->con->prepare("INSERT INTO `goods`
														(`good_name`, `quantity_level`,
															`is_to_buy`, `sync_status`, `device_good_id`, `device_category_id`, `email`, `crud_status`, `server_category_id`, good_desc, server_originator_id)
											VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
				$stmt->bind_param("sssssssssss", $goodName, $quantityLevelId, $isToBuy, $sync,
												$deviceGoodId, $deviceCategoryId, $email,
												$crud, $serverCategoryId, $goodDesc, $serverOriginatorId);
					
				if (!$stmt->execute()) { $all_query_Ok = false; }
						
			}

			if ($all_query_Ok){
				mysqli_commit($this->con);
				return $this->SUCCESS;
			} else {
				mysqli_rollback($this->con);
				return $this->ERROR;
			}

		}
		

		public function addGood($good_name, $device_category_id, $quantity_level, $is_to_buy, $sync_status, $device_good_id, $email, $server_category_id){
	
			if (!($this->goodExists($device_good_id, $email))){
				$all_query_Ok = true;
				mysqli_autocommit($this->con,FALSE);
				
				$stmt = $this->con->prepare("INSERT INTO `goods` 
				(`good_name`, `device_category_id`, `quantity_level`, `is_to_buy`, `sync_status`, `device_good_id`, `email`, `server_category_id` )
				VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
				
				$stmt->bind_param("ssssssss", $good_name, $device_category_id, $quantity_level, $is_to_buy, $sync_status, $device_good_id, $email, $server_category_id );
				
				if (!$stmt->execute()) { $all_query_Ok = false; }
				
				if ($all_query_Ok){
					mysqli_commit($this->con);
					return $this->SUCCESS;
				} else {
					//mysqli_rollback($this->con);
					return $this->ERROR;
				}
			} else {
				return $this->EXISTING_ITEM;
			}
			


		}
		
		
		public function registerUser($email, $password, $user_name, $signUpType){
			
			if ($this->userExists($email, $signUpType)){
				return $this->EXISTING_ITEM;
			} else {
				
				$all_query_Ok = true;
				mysqli_autocommit($this->con,FALSE);
				
				$stmt = $this->con->prepare("INSERT INTO `users` 
				(`email`, `password`, `user_name`, `update_available`, sign_up_type )
				VALUES (?, ?, ?, 0, ?)");
				
				$stmt->bind_param("ssss", $email, $password, $user_name, $signUpType);
				
				if (!$stmt->execute()) { $all_query_Ok = false; }
				
				if ($all_query_Ok){
					mysqli_commit($this->con);
					return $this->SUCCESS;
				} else {
					mysqli_rollback($this->con);
					return $this->ERROR;
				}
				
			}
		}		
		
		
		public function addGroup($group_name, $owner_email, $server_owner_id, $sync_status){
			
			if ($this->groupExists($group_name, $server_owner_id)){
				return $this->EXISTING_ITEM;
			} else {
				
				$all_query_Ok = true;
				mysqli_autocommit($this->con,FALSE);
				
				$stmt = $this->con->prepare("INSERT INTO `groups` 
				(`group_name`, `owner_email`, `server_owner_id`, `sync_status`)
				VALUES (?, ?, ?, ?);");
				
				$stmt->bind_param("ssss", $group_name, $owner_email, $server_owner_id, $sync_status);
				
				
				if (!$stmt->execute()) { $all_query_Ok = false; }
				
				if ($all_query_Ok){
					mysqli_commit($this->con);
					return $this->SUCCESS;
				} else {
					//mysqli_rollback($this->con);
					return $this->ERROR;
				}
				
			}
		}	
		

		
		
		// A - 3 - Deleting functions
		

		
		// A - 4 - Updanting functions
		
		public function updateCoUser($server_co_user_id, $confirmation_status, $has_responded){
			
			$all_query_Ok = true;
			mysqli_autocommit($this->con,FALSE);
			
			$stmt = $this->con->prepare("UPDATE `co_users` SET confirmation_status=?, has_responded=?
										WHERE server_co_user_id=?");
			
			$stmt->bind_param("sss", $confirmation_status, $has_responded, $server_co_user_id  );
			
			if (!$stmt->execute()) { $all_query_Ok = false; }
			
			if ($all_query_Ok){
				mysqli_commit($this->con);
				return $this->SUCCESS;
			} else {
				//mysqli_rollback($this->con);
				return $this->ERROR;
			}
			
		}
		
		public function updateUserGroup($server_user_id, $server_group_id){
			$all_query_Ok = true;
			mysqli_autocommit($this->con,FALSE);
			
			$stmt = $this->con->prepare("UPDATE `users` SET server_group_id=?
										WHERE server_user_id=?");
			
			$stmt->bind_param("ss", $server_group_id, $server_user_id );
			
			if (!$stmt->execute()) { $all_query_Ok = false; }
			
			if ($all_query_Ok){
				mysqli_commit($this->con);
				return $this->SUCCESS;
			} else {
				//mysqli_rollback($this->con);
				return $this->ERROR;
			}
		
		}
		
		
		public function updateAvailableUpdateforUser($server_user_id){
			$all_query_Ok = true;
			mysqli_autocommit($this->con,FALSE);
			
			$stmt = $this->con->prepare("UPDATE `users` SET update_available = 0
										WHERE server_user_id=?");
			
			$stmt->bind_param("s",$server_user_id );
			
			if (!$stmt->execute()) { $all_query_Ok = false; }
			
			if ($all_query_Ok){
				mysqli_commit($this->con);
				return $this->SUCCESS;
			} else {
				//mysqli_rollback($this->con);
				return $this->ERROR;
			}
		
		}
		
		public function updateCategoriesAndGoods($updatedCategoriesOnMobile, $updatedGoodsOnMobile, $currentServerUserId){
			
			$all_query_Ok = true;
			mysqli_autocommit($this->con,FALSE);
			
			$updatedRowsNumber = 0;
			
			if (count($updatedCategoriesOnMobile) > 0 ) {
			
				foreach($updatedCategoriesOnMobile as $updatedCategory) {
					
					$categoryName = $updatedCategory['categoryName'];
					$serverCategoryId = $updatedCategory['serverCategoryId'];
					$color = $updatedCategory['color'];
					$icon = $updatedCategory['icon'];
					$sync = $updatedCategory['sync'];
					$crud = $updatedCategory['crud_status'];
					$email = $updatedCategory['email'];
					
					

					$stmt1 = $this->con->prepare("UPDATE `categories` SET 
													`category_name`= ?,
													`category_color`= ?,
													`category_icon`= ?,
													`sync_status`= ?,
													`crud_status` = ?,
													`email`= ?,
													`last_update_user_id`= ?
													 WHERE `server_category_id` = ?");

					$stmt1->bind_param("ssssssss", $categoryName, $color, $icon, $sync, $crud, $email, $currentServerUserId , $serverCategoryId );
					
					
					if (!$stmt1->execute()) { $all_query_Ok = false; }
					
					$updatedRowsNumber = $updatedRowsNumber +1;
					
				}
				
			}
			
			if (count($updatedGoodsOnMobile) > 0 ) {

				foreach($updatedGoodsOnMobile as $updatedGood) {

					$stmt2 = $this->con->prepare("UPDATE `goods` SET
												`good_name`=?,
												`quantity_level`=?,
												`is_to_buy`=?,
												`sync_status`=?,
												`crud_status`=?,
												`email`=?,
												`server_category_id`=?,
												`last_update_user_id`= ?,
												`good_desc` = ?
												WHERE `server_good_id`=?");
																						
					$stmt2->bind_param("ssssssssss", $updatedGood['goodName'], $updatedGood['quantityLevelId'],
													$updatedGood['isToBuy'], $updatedGood['sync'],
													$updatedGood['crud_status'], $updatedGood['email'],
													$updatedGood['serverCategoryId'], $currentServerUserId ,
													$updatedGood['goodDesc'], $updatedGood['serverGoodId'] );
							
					if (!$stmt2->execute()) { $all_query_Ok = false; }
					
					$updatedRowsNumber = $updatedRowsNumber +1;
					
				}
			
			}

			//updating the update available column in the user table if there was an update

			if ($updatedRowsNumber>0) {

				$serverGroupId = $this->getGroupIdByUserId($currentServerUserId);
				
				$stmt3 = $this->con->prepare("UPDATE `users` as u, groups as gr SET u.`update_available`=1
												WHERE u.server_group_id = gr.server_group_id
												AND u.server_group_id = ?
												AND u.server_user_id <> ?");

				$stmt3->bind_param("ss", $serverGroupId, $currentServerUserId);
				
				if (!$stmt3->execute()) { $all_query_Ok = false; }
					
			}
			
			if ($all_query_Ok){
				mysqli_commit($this->con);
				return $this->SUCCESS;
			} else {
				mysqli_rollback($this->con);
				return $this->ERROR;
			}
		
		}
		
		

		// A - 4 - Deleting functions
		public function deleteAllUserCategoriesAndGoods($server_user_id){
			$all_query_Ok = true;
			mysqli_autocommit($this->con,FALSE);
			
			$stmt1 = $this->con->prepare("DELETE goods
											FROM goods
											INNER JOIN categories 
											ON categories.server_category_id=goods.server_category_id
											WHERE categories.server_user_id = ?");
			$stmt1->bind_param("s", $server_user_id );
			
			$stmt2 = $this->con->prepare("DELETE FROM categories where server_user_id = ?");
			$stmt2->bind_param("s", $server_user_id );
			
			if (!$stmt1->execute()) { $all_query_Ok = false; }
			if (!$stmt2->execute()) { $all_query_Ok = false; }
			
			if ($all_query_Ok){
				mysqli_commit($this->con);
				return $this->SUCCESS;
			} else {
				mysqli_rollback($this->con);
				return $this->ERROR;
			}
		
		}

		
		
		public function deleteAll(){
			$all_query_Ok = true;
			mysqli_autocommit($this->con,FALSE);
			
			$stmt1 = $this->con->prepare("DELETE FROM `goods` WHERE goods.server_originator_id <> 1");
			$stmt2 = $this->con->prepare("DELETE FROM `categories` WHERE categories.server_user_id <> 1");
			$stmt3 = $this->con->prepare("DELETE FROM `co_users` WHERE 1");
			$stmt4 = $this->con->prepare("DELETE FROM `users` WHERE users.server_user_id <> 1");
			$stmt5 = $this->con->prepare("DELETE FROM `groups` WHERE 1");
			
			
			if (!$stmt1->execute()) { $all_query_Ok = false; }
			if (!$stmt2->execute()) { $all_query_Ok = false; }
			if (!$stmt3->execute()) { $all_query_Ok = false; }
			if (!$stmt4->execute()) { $all_query_Ok = false; }
			if (!$stmt5->execute()) { $all_query_Ok = false; }
			
			if ($all_query_Ok){
				mysqli_commit($this->con);
				return $this->SUCCESS;
			} else {
				mysqli_rollback($this->con);
				return $this->ERROR;
			}
		
		}		
		
		
		// B  - private functions
		
		// B - 1 - Reading functions
		
		
			
		private function groupExists($group_name, $server_owner_id){
			$stmt = $this->con->prepare("SELECT * FROM `groups` WHERE `group_name` = ? AND `server_owner_id` = ?");
			$stmt->bind_param("ss",$group_name, $server_owner_id);
			$stmt->execute();
			$stmt->store_result();
			return $stmt->num_rows > 0;
		}	
	
	
		private function coUserExists($email, $co_user_email){
			$stmt = $this->con->prepare("SELECT * FROM `co_users` WHERE `email` = ? AND `co_user_email` = ?");
			$stmt->bind_param("ss",$email, $co_user_email);
			$stmt->execute();
			$stmt->store_result();
			return $stmt->num_rows > 0;
		}

		
		private function categoryExists($device_category_id, $email){
			$stmt = $this->con->prepare("SELECT * FROM `categories` WHERE `device_category_id` = ? AND `email` = ?");
			$stmt->bind_param("ss",$device_category_id, $email);
			$stmt->execute();
			$stmt->store_result();
			return ($stmt->num_rows > 0);
		}
		
		private function goodExists($device_good_id, $email){
			$stmt = $this->con->prepare("SELECT * FROM `goods` WHERE `device_good_id` = ? AND `email` = ? ");
			$stmt->bind_param("ss",$device_good_id, $email);
			$stmt->execute();
			$stmt->store_result();
			return $stmt->num_rows > 0;
		}
		
		private function userExists($email, $signUpType){
			$stmt = $this->con->prepare("SELECT * FROM `users` WHERE `email` = ? AND sign_up_type = ?");
			$stmt->bind_param("ss",$email, $signUpType);
			$stmt->execute();
			$stmt->store_result();
			return $stmt->num_rows > 0;
		}
		
		// B - 2 Creating functions
		
		
		// B - 3 - Deleting functions
		
		
		// B - 4 - Updating functions
		
		
		
		
	}

?>