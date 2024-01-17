<#-- @ftlvariable name="videos" type="kotlin.collections.List<net.grandcentrix.backend.models.Video>" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <iframe width="75%" height="75%"
            src="https://www.youtube.com/embed/${video.id}">
    </iframe>

    <#list 1..videos as video>
        <ul>
            <li>${video.title}</li>
        </ul>
    <#--            <h2>The video at index ${item?index} is ${item}</h2>-->
    </#list>

    <button>
        <a href="/" style="font-size: 25px; text-decoration: none">Shuffle</a>
    </button>
    <button>
        <a href="/" style="font-size: 25px; text-decoration: none">Add a video</a>
    </button>
    <#--<button><a href="/" style="font-size: 25px; text-decoration: none">Delete a video</a></button>-->
</@layout.header>
