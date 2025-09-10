const { db } = require("../firebase");

exports.signup = async (req, res) => {
  try {
    const { name, upiId } = req.body;

    if (!name || !upiId) {
      return res.status(400).json({ message: "Name and UPI ID are required" });
    }

    const userRef = db.collection("users").doc(req.user.uid);
    const userDoc = await userRef.get();

    if (userDoc.exists) {
      return res.status(400).json({ message: "User already exists" });
    }

    await userRef.set({
      uid: req.user.uid,
      name,
      email: req.user.email,
      upiId,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    });

    return res.status(201).json({ message: "User created successfully" });
  } catch (error) {
    console.error("Signup error:", error);
    return res.status(500).json({ message: "Internal Server Error" });
  }
};

exports.getUserProfile = async (req, res) => {
  try {
    const userRef = db.collection("users").doc(req.user.uid);
    const userDoc = await userRef.get();

    if (!userDoc.exists) {
      return res.status(404).json({ message: "User profile not found" });
    }

    return res.status(200).json(userDoc.data());
  } catch (error) {
    console.error("Get user profile error:", error);
    return res.status(500).json({ message: "Internal Server Error" });
  }
};
