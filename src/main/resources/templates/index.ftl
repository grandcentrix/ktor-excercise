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
                <h2>${actionTitle}</h2>
                <form action="${buttonAction}" method="POST">
                    <#if actionTitle == "Add a new video:">
                        <label>
                            <input placeholder="Insert video link" type="text" name="link">
                        </label>
                    </#if>
                    <#if actionTitle == "Update video title:">
                        <label>
                            ${link}
                        </label>
                    </#if>


                    <label>
                        <input placeholder="Insert video title" type="text" name="title">
                    </label>

                    <p>
                        <label for="videoTypes">Choose a type:</label>
                        <select name="videoTypes" id="videoTypes">
                            <#list videoType as type>
                                <option value="${type}">${type}</option>
                            </#list>
                        </select>
                    </p>

                    <input class="button" type="submit">

                </form>
                <p>${status}</p>
            </section>

            <h2>Added videos: </h2>
            <ul class="videos-list">

                <#list videos as video>
                    <li>
                        <span>
                            <a href="https://www.youtube.com/watch?v=${video.id}">${video.title}</a>
                            <a class="button-type" href="${video.videoType}">${video.videoType}</a>
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
