<?php

//$connection = mysqli_connect("localhost", "root", "", "lockmate");
$connection = mysqli_connect("localhost", "arbna", "a13cs0175", "db_arbna");


if (isset($_POST["studentName"], $_POST["matricNo"], $_POST["password"])) {
    $studentName = $_POST["studentName"];
    $matricNo = $_POST["matricNo"];
    $password = $_POST["password"];
    header('Content-Type: application/json');

    if (!empty($studentName) && !empty($matricNo) && !empty($password)) {
        $query = "INSERT INTO qr_keys(studentName, matricNo, password) VALUES('$studentName', '$matricNo', '$password')";

        $result = mysqli_query($connection, $query);

        // check if row inserted or not
        if ($result) {
            // successfully inserted into database
            echo "Key successfully created.";
        }
        else {
            // failed to insert row
            echo "Oops! An error occured.";
        }

    }
    else {
        echo "You must fill all fields (studentName, matricNo & password)";
    }
}
?>