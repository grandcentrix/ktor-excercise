<#import "_layout.ftl" as layout />
<@layout.header>

        <section class="container-left">
            <iframe width="100%" height="550px"
                    src="https://www.youtube.com/embed/${randomId}">
            </iframe>

            <a class="shuffle" href="/${videoType}/shuffle">Shuffle</a>
        </section>

        <section class="container-right">

            <section class="new-video">

                <h2>${formAction["name"]}</h2>

                <form action="/${videoType}${formAction["link"]}" method="POST">

                    <#if formAction["type"] == "ADD">
                        <label>
                            <input placeholder="Insert video link" type="text" name="link">
                        </label>
                    </#if>
                    <#if formAction["type"] == "UPDATE">
                        <label>
                            ${video.link}
                        </label>
                    </#if>

                    <label>
                        <input placeholder="Insert video title" type="text" name="title">
                    </label>

                    <p>
                        <label for="videoTypes">Choose a type:</label>
                        <select name="videoTypes" id="videoTypes">
                            <option name="type" value="${videoType}">${videoType}</option>
                        </select>
                    </p>

                    <input class="button" type="submit">

                </form>
                <#if formAction["type"] == "UPDATE">
                    <button class="button"><a href="${video.id}/update/cancel">Cancel</a></button>
                </#if>
                <p>${status}</p>
            </section>

            <div style="display: inline-flex;justify-content: space-between;align-items: center;">
                <h2>Added videos: </h2>
                <h3>
                    <a href="/">Return to all videos list</a>
                </h3>
            </div>
            <ul class="videos-list">

                <#list videos as video>
                    <li>
                        <span style="display: flex;align-items: center;">
                            <a href="https://www.youtube.com/watch?v=${video.id}">${video.title}</a>
                            <a class="button-type" href="">${video.videoType}</a>
                        </span>

                        <span>
                            <a class="button" href="${video.id}/update">Edit</a>
                            <a class="button" href="${video.id}/delete">Delete</a>
                        </span>
                    </li>
                </#list>
            </ul>

        </section>

</@layout.header>
