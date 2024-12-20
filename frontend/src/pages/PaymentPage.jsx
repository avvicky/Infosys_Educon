import React, { useState } from "react";
import axios from "axios";
import { getUser } from "../services/authService";

const PaymentPage = ({ product }) => {
  const [loading, setLoading] = useState(false);
  const user = getUser();

  const handlePayment = async () => {
    setLoading(true);

    try {
      // Step 1: Create an order in the backend
      const orderResponse = await axios.post(
        "http://localhost:8080/payments/create",
        {
          productId: product.id, // Replace with actual product ID
        }
      );

      const { id: order_id, amount, currency } = orderResponse.data;

      // Step 2: Initialize Razorpay
      const options = {
        key: "YOUR_RAZORPAY_KEY_ID", // Replace with your Razorpay Key ID
        amount: amount, // Amount in paise
        currency: currency,
        name: "Edu_Consultancy",
        description: product.title,
        order_id: order_id,
        handler: async (response) => {
          // Verify payment signature in the backend
          const verificationResponse = await axios.post(
            "http://localhost:8080/payments/verify",
            {
              razorpay_payment_id: response.razorpay_payment_id,
              razorpay_order_id: response.razorpay_order_id,
              razorpay_signature: response.razorpay_signature,
            }
          );

          if (verificationResponse.data === "Successful") {
            alert("Payment Successful!");
          } else {
            alert("Payment Failed!");
          }
        },
        prefill: {
          name: user.name, // Replace with dynamic data if needed
          email: user.sub, // Replace with dynamic data if needed
        },
        theme: {
          color: "#6c5ce7", // Customize button color
        },
      };

      const razorpay = new window.Razorpay(options);
      razorpay.open();
    } catch (error) {
      console.error("Payment Error:", error);
      alert("Something went wrong. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-center p-6 bg-gray-100 rounded-lg shadow-md">
      <h2 className="text-xl font-semibold text-gray-800 mb-4">
        {product.title}
      </h2>
      <p className="text-gray-600 mb-6">{product.description}</p>
      <div className="text-lg font-bold text-gray-900 mb-4">
        ₹ {product.price}
      </div>
      <button
        onClick={handlePayment}
        disabled={loading}
        className={`py-2 px-4 rounded bg-purple-600 text-white font-semibold hover:bg-purple-500 ${
          loading ? "opacity-50 cursor-not-allowed" : ""
        }`}
      >
        {loading ? "Processing..." : "Buy Now"}
      </button>
    </div>
  );
};

export default PaymentPage;
