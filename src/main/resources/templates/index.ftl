<#import "_layout.ftl" as layout />
<@layout.header>

        <section class="container-left">
            <iframe width="100%" height="550px"
                    src="https://www.youtube.com/embed/${randomId}">
            </iframe>

            <a class="shuffle" href="/shuffle">Shuffle</a>
        </section>

        <section class="container-right">

            <section class="new-video">

                <h2>${formAction["name"]}</h2>

                <form action="${formAction["link"]}" method="POST">

                    <#if formAction["type"] == "ADD">
                        <label>
                            <input placeholder="Insert video link" type="text" name="link">
                        </label>
                    </#if>
                    <#if formAction["type"] == "UPDATE">
                        <p class="actualValues" style="margin-top: 0">
                            <label for="videoTypes">Video Link: <br> </label>
                            <span>
                                <a href="${video.link}">${video.link}</a>
                            </span>
                            <br><br>
                            <label for="videoTypes">Current title: <br> </label>
                            <span style="font-weight: normal">
                                ${video.title}
                            </span>
                        </p>

                    </#if>
                    <label>
                        <input placeholder="Insert video title" type="text" name="title">
                    </label>

                    <p style="margin-top: 0">
                        <label for="videoTypes">Choose a type:</label>
                        <select name="videoTypes" id="videoTypes">
                            <#list videoTypes as type>
                                <option name="type" value="${type}">${type}</option>
                            </#list>
                        </select>

                        <label>
                            <input placeholder="Custom type name" type="text" name="customType">
                        </label>
                    </p>
                    <input class="button" type="submit">
                </form>
                <#if formAction["type"] == "UPDATE">
                    <button class="button"><a href="${video.id}/update/cancel">Cancel</a></button>
                </#if>
                <p>${status}</p>
            </section>

            <h2>Added videos: </h2>
            <ul class="videos-list">

                <#list videos as video>
                    <li>
                        <span style="display: flex;align-items: center;">
                            <a href="https://www.youtube.com/watch?v=${video.id}">${video.title}</a>
                            <#if video.videoType == "CUSTOM">
                                <a class="button-type" href="${video.customTypeName}/videos">${video.customTypeName}</a>
                            </#if>
                            <#if video.videoType != "CUSTOM">
                                <a class="button-type" href="${video.videoType}/videos">${video.videoType}</a>
                            </#if>
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
