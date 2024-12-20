import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getBlogById } from "../services/blogService";

function BlogDetail() {
  const { id } = useParams(); // Extract blog ID from the URL
  const [blog, setBlog] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Fetch blog data when the component mounts
    const fetchBlog = async () => {
      try {
        const response = await getBlogById(id);
        console.log(response);
        setBlog(response); // Set the fetched blog data
      } catch (err) {
        setError("Failed to fetch blog data. Please try again later.");
      } finally {
        setLoading(false); // Stop loading
      }
    };

    fetchBlog();
  }, [id]);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div>
      <h1>{blog.title}</h1>
      <img
        src={blog.heroImg}
        alt={blog.title}
        style={{ width: "100%", maxHeight: "400px" }}
      />
      <p>
        <strong>Author:</strong> {blog.author}
      </p>
      <p>
        <strong>Published At:</strong>{" "}
        {new Date(blog.publishedAt).toLocaleDateString()}
      </p>
      <div dangerouslySetInnerHTML={{ __html: blog.content }} />
      <div>
        <h3>Tags:</h3>
        <ul>
          {blog.tags &&
            blog.tags.map((tag) => <li key={tag.id}>{tag.tagName}</li>)}
        </ul>
      </div>
    </div>
  );
}

export default BlogDetail;
