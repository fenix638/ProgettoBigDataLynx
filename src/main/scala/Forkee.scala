class Forkee(
                   val archive_url:String,
                   val archived:Boolean,
                   val assignees_url:String,
                   val blobs_ur:String,
                   val branches_url:String,
                   val clone_url:String,
                   val collaborators_url:String,
                   val comments_url:String,
                   val commits_url:String,
                   val compare_url:String,
                   val contents_url:String,
                   val contributors_url:String,
                   val created_at:String,
                   val default_branch:String,
                   val deployments_url:String,
                   val description:String,
                   val downloads_url:String,
                   val events_url:String,
                   val fork:Boolean,
                   val forks:Long,
                   val forks_count:Long,
                   val forks_url:String,
                   val full_name:String,
                   val git_commits_url:String,
                   val git_refs_url:String,
                   val git_tags_url:String,
                   val git_url:String,
                   val has_downloads:Boolean,
                   val has_issues:Boolean,
                   val has_pages:Boolean,
                   val has_projects:Boolean,
                   val has_wiki:Boolean,
                   val homepage:String,
                   val hooks_url:String,
                   val html_url:String,
                   val id: Long,
                   val issue_comment_url:String,
                   val issue_events_url:String,
                   val issues_url:String,
                   val keys_url:String,
                   val labels_url:String,
                   val language:String,
                   val languages_url:String,
                   val license: License,
                   val merges_url:String,
                   val milestones_url:String,
                   val mirror_url:String,
                   val name:String,
                   val notifications_url:String,
                   val open_issues:Long,
                   val open_issues_count:Long,
                   val owner:Owner,
                   val `private`:Boolean,
                   val `public`:Boolean,
                   val pulls_url:String,
                   val pushed_at:String,
                   val releases_url:String,
                   val size:Long,
                   val ssh_url:String,
                   val stargazers_count:Long,
                   val stargazers_url:String,
                   val statuses_url:String,
                   val subscribers_url:String,
                   val subscription_url:String,
                   val svn_url:String,
                   val tags_url:String,
                   val teams_url:String,
                   val trees_url:String



                 )extends Product with Serializable{
  def canEqual(that: Any) = that.isInstanceOf[Forkee]
  def productArity = 23 // number of columns

  def productElement(idx: Int) = idx match {
    case 0 => field1
    case 1 => field2
    // .. and so on ..
    case 22 => field23
  }

}
