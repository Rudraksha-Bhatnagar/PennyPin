const { auth } = require("../firebase");

const verifyFirebaseToken = async (req, res, next) => {
  try {
    const header = req.headers.authorization;

    if (!header || !header.startsWith("Bearer ")) {
      return res.status(401).json({ message: "No token provided" });
    }

    const idToken = header.split(" ")[1];
    const decodedToken = await auth.verifyIdToken(idToken, true);

    req.user = {
      uid: decodedToken.uid,
      email: decodedToken.email,
      name: decodedToken.name || null,
      picture: decodedToken.picture || null,
      email_verified: decodedToken.email_verified,
    };

    if (!req.user.email_verified) {
      return res.status(403).json({ message: "Email not verified" });
    }

    next();
  } catch (error) {
    console.error("Token verification failed:", error);
    let message = "Unauthorized";

    if (error.code === "auth/id-token-expired") {
      message = "Token expired. Please log in again.";
    }

    return res.status(401).json({ message });
  }
};

module.exports = verifyFirebaseToken;
