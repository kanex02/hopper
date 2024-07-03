const timeout = 500
const feed = document.getElementById("mainFeed")
let basePathUrl = ''
let lastRequestTime = 0
let postsSoFar = 0


function populateChallengeDetails(jsonChallenge, clone, postID) {
    // Get the elements from the cloned content
    const anchorElement = clone.querySelector('#viewChallengeDetailsTitle');
    const titleElement = clone.querySelector('.challenge-title');
    const descriptionElement = clone.querySelector('.challenge-description');
    const collapse = clone.querySelector('.collapse')
    collapse.setAttribute('id', `challengeDetails-${postID}`)

    // Populate anchor and div attributes
    anchorElement.setAttribute('href', `#challengeDetails-${postID}`);
    anchorElement.setAttribute('aria-controls', `challengeDetails-${postID}`);
    anchorElement.setAttribute('aria-expanded', 'false');
    anchorElement.setAttribute('role', 'button');
    anchorElement.setAttribute('data-bs-toggle', 'collapse');

    // Populate title and description
    const challengeTitleText = document.createTextNode(jsonChallenge.title);
    titleElement.appendChild(challengeTitleText);

    const hopsText = document.createTextNode(` (+${jsonChallenge.hops} Hops)`);
    titleElement.appendChild(hopsText);

    descriptionElement.textContent = jsonChallenge.goal;
}

function populateTemplate(jsonBlogPost) {
    const template = document.querySelector("#blog-post-template");
    const clone = document.importNode(template.content, true);

    const title = clone.querySelector(".card-title");
    title.textContent = jsonBlogPost.title;

    const cardText = clone.querySelector(".card-text");
    cardText.textContent = jsonBlogPost.content;


    const userProfileImg = clone.querySelector(".user-profile #profilePicture");
    const userBorderProfileImg = clone.querySelector(".user-profile #borderPicture");
    const userInfo = clone.querySelector(".user-info");
    const authorLink = document.createElement('a');
    const userProfileDiv = clone.querySelector(".user-profile");

    let proxyLink = null;
    console.log(jsonBlogPost);
    if (jsonBlogPost.proxyClub != null){
        proxyLink = document.createElement('a');
        console.log('club exists!');
        if (jsonBlogPost.proxyClub.picture)
        {
            userProfileImg.src = basePathUrl + jsonBlogPost.proxyClub.picturePath;
        } else {
            userProfileImg.src = basePathUrl + "images/defaultClubPicture.png";
        }
        authorLink.href = basePathUrl + `user/${jsonBlogPost.author.id}`;
        authorLink.textContent = `${jsonBlogPost.author.firstName} ${jsonBlogPost.author.lastName}`;
        proxyLink.href = basePathUrl + `club/${jsonBlogPost.proxyClub.id}`;
        proxyLink.textContent = `${jsonBlogPost.proxyClub.name}`;

        userProfileDiv.addEventListener("click", () => window.location.href = basePathUrl + `club/${jsonBlogPost.proxyClub.id}`);

    } else if (jsonBlogPost.proxyTeam != null) {
        proxyLink = document.createElement('a');
        if (jsonBlogPost.proxyTeam.picture)
        {
            userProfileImg.src = basePathUrl + jsonBlogPost.proxyTeam.picturePath;
        } else {
            userProfileImg.src = basePathUrl + "images/defaultPictureTeam.png";
        }
        authorLink.href = basePathUrl + `user/${jsonBlogPost.author.id}`;
        authorLink.textContent = `${jsonBlogPost.author.firstName} ${jsonBlogPost.author.lastName}`;

        userProfileDiv.addEventListener("click", () => window.location.href = basePathUrl + `teams/${jsonBlogPost.proxyTeam.id}`);
        proxyLink.href = basePathUrl + `teams/${jsonBlogPost.proxyTeam.id}`;
        proxyLink.textContent = `${jsonBlogPost.proxyTeam.name}`;

    } else {
        if (jsonBlogPost.author && jsonBlogPost.author.profilePicturePath)
        {
            userProfileImg.src = basePathUrl + jsonBlogPost.author.profilePicturePath;
        } else {
            userProfileImg.src = basePathUrl + "images/defaultPicture.png";
        }
        if (jsonBlogPost.author.borderImageName){
            userBorderProfileImg.src = basePathUrl + jsonBlogPost.author.borderImageName;
            userBorderProfileImg.hidden = false;
        }

        userProfileDiv.addEventListener("click", () => window.location.href = basePathUrl + `user/${jsonBlogPost.author.id}`);
        authorLink.href = basePathUrl + `user/${jsonBlogPost.author.id}`;
        authorLink.textContent = `${jsonBlogPost.author.firstName} ${jsonBlogPost.author.lastName}`;
    }

    const dateText = document.createTextNode(` ${jsonBlogPost.date}`);

    if (proxyLink){
        userInfo.appendChild(proxyLink);
        const spacer = document.createElement('a');
        spacer.textContent = ' - '
        userInfo.appendChild(spacer);
    }
    userInfo.appendChild(authorLink);
    userInfo.appendChild(dateText);


    const editButton = clone.querySelector(".edit-button");
    if (jsonBlogPost.editable) {
        editButton.onclick = function() {
            window.location.href = basePathUrl + 'blog/edit/' + jsonBlogPost.blogPostId
        };
    } else {
        editButton.remove();
    }

    const deleteButton = clone.querySelector(".delete-button");
    if (jsonBlogPost.deleteable) {
        deleteButton.onclick = function(event) {
            openDeleteConfirmation(jsonBlogPost.blogPostId);
            event.stopPropagation();
        };
    } else {
        deleteButton.remove();
    }

    if (jsonBlogPost.challenge) {
        populateChallengeDetails(jsonBlogPost.challenge, clone, jsonBlogPost.blogPostId);
    } else {
        clone.querySelector("#viewChallengeDetailsTitle").remove()
    }

    const mediaWrapper = clone.querySelector("#mediaWrapper");

    if (jsonBlogPost.filePath) {
        const media = jsonBlogPost.mediaType === 'IMAGE'
            ? document.createElement("img")
            : document.createElement("video");
        media.src = basePathUrl + jsonBlogPost.filePath;
        media.classList.add("blog-post-media");
        if (media.tagName === 'VIDEO') {
            media.setAttribute("controls", "true");
        }
        mediaWrapper.appendChild(media);
    } else {
        mediaWrapper.remove()
    }

    return clone
}

async function fetchBlogPosts(basePath=basePathUrl, following=false) {
    basePathUrl = basePath

    let time = new Date().getTime()
    if (time < lastRequestTime + timeout) {
        return
    }
    lastRequestTime = time
    await fetch(basePathUrl + `api/blog?postsSoFar=${postsSoFar}&following=${following}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Unable to get response from server")
            }

            return response.json()
        })
        .then(data => {
            postsSoFar += data.length

            for (let post of data) {
                feed.appendChild(populateTemplate(post))
            }
        });
}
