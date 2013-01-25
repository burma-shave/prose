package model.posts

import java.util.Date


sealed trait BlogPost

case class ExistingUserIds(
                            userIds: List[String]
)

case class DraftBlogPost(
                          id: String,
                          title: String,
                          content: String,
                          tags: Array[String],
                          author: String,
                          order: Int
                          )

case class PublishedBlogPost(
                              id: String, publishedDate: Date
                              )


sealed trait BlogPostEvent {
  val id: String
}


case class UserAdded(id: String)
case class BlogPostCreated(id: String, author: String, title: String)
case class BlogPostContentUpdated(id: String, title: String, content: String)
case class BlogPostTagsUpdated(id: String, tags: Array[String])
case class BlogPostPublished(id: String)
case class BlogPostUnPublished(id: String)
case class BlogPostDeleted(id: String)



