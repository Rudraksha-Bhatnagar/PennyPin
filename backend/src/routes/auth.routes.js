const express = require("express");
const verifyFirebaseToken = require("../middleware/auth.middleware");
const { signup, getUserProfile } = require("../controllers/auth.controller");

const router = express.Router();

router.post("/signup", verifyFirebaseToken, signup);
router.get("/profile", verifyFirebaseToken, getUserProfile);

module.exports = router;
