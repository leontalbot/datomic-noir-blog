(ns noir-blog.views.blog
  (:use noir.core
        hiccup.core
        hiccup.element
        hiccup.page)
  (:require [noir-blog.models.post :as posts]
            [noir-blog.views.common :as common]
            [noir-blog.models.user :as user]
            [noir.response :as resp]))

;; Page structure

(defpartial post-item [{:keys [moniker title body date tme] :as post}]
            (when post
              [:li.post
               [:h2 (link-to (posts/perma-link moniker) title)]
               [:ul.datetime
                [:li date]
                [:li tme]
                (when (user/admin?)
                  [:li (link-to (posts/edit-url post) "edit")])]
               [:div.content (posts/md->html body)]]))

(defpartial blog-page [items]
            (common/main-layout
              [:ul.posts
               (map post-item items)]))

;; Blog pages

(defpage "/" []
         (resp/redirect "/blog/"))

(defpage "/blog/" []
         (blog-page (posts/get-latest)))

(defpage "/blog/page/:page" {:keys [page]}
         (blog-page (posts/get-page page)))

(defpage "/blog/page/" []
         (render "/blog/page/:page" {:page 1}))

(defpage "/blog/view/:perma" {:keys [perma]}
         (if-let [cur (posts/moniker->post perma)]
           (blog-page [cur])))
