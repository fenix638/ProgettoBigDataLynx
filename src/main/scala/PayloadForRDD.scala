case class PayloadForRDD(
                  action:String,
                  before:String,
                  comment: CommentForRDD,
                  commits: CommitPrincipale,
                  description:String,
                  distinct_size:BigInt,
                  forkee: ForkeeForRDD,
                  head:String,
                  issue: IssuePrincipaleForRDD,
                  master_branch:String,
                  member: MemberForRDD,
                  number:BigInt,
                  pages: Pages,
                  pull_request: PullRequestPrincipale,
                  push_id:BigInt,
                  pusher_type:String,
                  ref:String,
                  ref_type:String,
                  release: ReleaseForRDD,
                  size:BigInt
                  )